package cn.cenxt.task.scheduler;

import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.service.CenxtTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行包装类
 */
public class ExecWrapper implements Runnable {

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

    public ExecWrapper(Task task, CenxtJob job, CenxtTaskListener listener, CenxtTaskService cenxtTaskService) {
        this.task = task;
        this.job = job;
        this.listener = listener;
        this.cenxtTaskService = cenxtTaskService;
    }

    @Override
    public void run() {
        Thread thread = new Thread() {
            private final Logger logger = LoggerFactory.getLogger(task.getName());

            @Override
            public void run() {

                long start = System.currentTimeMillis();
                try {
                    //监听器异常不抛出
                    listener.begin(task);
                } catch (Exception e) {
                    logger.warn("listener begin error", e);
                }

                int retryTimes = Math.max(task.getRetryTimes(), 0);
                boolean result = false;
                StringBuilder erroMsg = new StringBuilder();
                int i = 0;
                for (; i <= retryTimes; i++) {
                    long step = System.currentTimeMillis();
                    if (i > 0) {
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
                        //中断退出任务
                        try {
                            listener.fail(task, System.currentTimeMillis() - step, i + 1, ex);
                        } catch (Exception e) {
                            logger.warn("listener fail error", e);
                        }
                        return;
                    } catch (Exception ex) {
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
                        listener.exceptionFinish(task, cost, i);
                    }
                } catch (Exception e) {
                    logger.warn("listener finish error", e);
                }
            }
        };

        thread.setName(task.getExecId());
        try {
            //超时检查
            if (task.getExpire() > 0) {
                thread.join(task.getExpire() * 60 * 1000);
                //超时后停止线程
                thread.interrupt();
            } else {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
