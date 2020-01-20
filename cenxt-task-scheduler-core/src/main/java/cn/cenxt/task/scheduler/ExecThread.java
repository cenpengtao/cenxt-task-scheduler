package cn.cenxt.task.scheduler;

import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.service.CenxtTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务执行线程
 */
public class ExecThread extends Thread {

    private final Logger logger;
    /**
     * 任务信息
     */
    private Task task;

    /**
     * 执行job
     */
    private CenxtJob job;

    /**
     * 任务监听器
     */
    private CenxtTaskListener listener;

    /**
     * 任务服务
     */
    private CenxtTaskService cenxtTaskService;

    public ExecThread(Task task, CenxtJob job, CenxtTaskListener listener, CenxtTaskService cenxtTaskService) {
        this.setName(task.getExecId());
        logger = LoggerFactory.getLogger(task.getName());
        this.task = task;
        this.job = job;
        this.listener = listener;
        this.cenxtTaskService = cenxtTaskService;
    }

    @Override
    public void run() {

        logger.info("task begin ,execId:{}", task.getExecId());
        long start = System.currentTimeMillis();
        boolean result = true;
        int i = 0;
        StringBuilder erroMsg = new StringBuilder();

        try {
            //监听器异常不抛出
            listener.begin(task);
        } catch (Exception e) {
            logger.warn("listener begin error", e);
        }
        int retryTimes = Math.max(task.getRetryTimes(), 0);

        for (; i <= retryTimes; i++) {
            long step = System.currentTimeMillis();
            if (i > 0) {
                logger.info("task retry ,execId:{},retryTimes:{}", task.getExecId(), i);
                //记录重试
                try {
                    listener.retry(task, i);
                } catch (Exception e) {
                    logger.warn("listener retry error", e);
                }
            }

            //执行任务
            try {
                job.pre(task);
                result = job.exec(task);
            } catch (InterruptedException ex) {
                logger.error("task interrupted ,execId:{}", task.getExecId());
                //中断退出任务
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, ex);
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
                break;
            } catch (Exception ex) {
                logger.error("task exec error, execId:{}", task.getExecId(), ex);
                erroMsg.append(ex.getMessage()).append("\n");
                //异常执行重试
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, ex);
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
                continue;
            }

            if (result) {
                //成功跳出循环
                break;
            } else {
                erroMsg.append("[").append(i).append("] ").append("job return error").append("\n");
                //job 返回执行异常
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, null);
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
            }
        }

        //执行结果：0成功 1重试后成功 2失败
        int execResult = 0;
        if (!result) {
            execResult = 2;
        } else if (i > 0) {
            execResult = 1;
        }
        logger.info("task finished ,execId:{},execResult:{}", task.getExecId(), execResult);
        try {
            //添加执行记录
            cenxtTaskService.insertExecHistory(task.getId(), task.getExecId(), task.getExecTime(),
                    cenxtTaskService.getNowTime(), execResult, erroMsg.toString());
        } catch (Exception e) {
            logger.error("insertExecHistory error", e);
        }
        try {
            //释放任务
            cenxtTaskService.releaseTask(task, execResult);
        } catch (Exception e) {
            logger.error("releaseTask error", e);
        }

        long cost = System.currentTimeMillis() - start;
        //调用任务结束事件
        try {
            if (result) {
                listener.finish(task, cost, i);
            } else {
                listener.exceptionFinish(task, cost, i - 1);
            }
        } catch (Exception e) {
            logger.warn("listener finish error", e);
        }
    }
}




