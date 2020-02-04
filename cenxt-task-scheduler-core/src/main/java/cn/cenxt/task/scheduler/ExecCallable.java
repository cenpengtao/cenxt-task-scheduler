package cn.cenxt.task.scheduler;

import cn.cenxt.task.enums.ExecResultEnum;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.ExecHistory;
import cn.cenxt.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.concurrent.Callable;

/**
 * 任务执行线程
 */
public class ExecCallable implements Callable<ExecHistory> {

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
     * 执行记录
     */
    private ExecHistory execHistory;


    public ExecCallable(Task task, CenxtJob job, CenxtTaskListener listener, ExecHistory execHistory) {
        logger = LoggerFactory.getLogger(task.getName());
        this.task = task;
        this.job = job;
        this.listener = listener;
        this.execHistory = execHistory;
    }

    @Override
    public ExecHistory call() throws Exception {
        logger.info("task begin ,execId:{}", task.getExecId());
        long start = System.currentTimeMillis();
        int i = 0;
        boolean result = true;
        StringBuilder message = new StringBuilder();
        try {
            //监听器异常不抛出
            listener.begin(task);
        } catch (Exception e) {
            logger.warn("listener begin error", e);
        }
        int retryTimes = Math.max(task.getRetryTimes(), 0);
        for (; i <= retryTimes; i++) {
            long step = System.currentTimeMillis();
            //初始化统计信息
            execHistory.getExecReport().setMessage("");
            execHistory.getExecReport().setSuccessCount(0);
            execHistory.getExecReport().setSuccessCount(0);
            if (i > 0) {
                execHistory.setRetryTimes(i);
                execHistory.setExecResult(ExecResultEnum.RETRYING.getResult());
                logger.info("task retry ,execId:{},retryTimes:{}", task.getExecId(), i);
                //记录重试
                try {
                    listener.retry(task, execHistory.getRetryTimes());
                } catch (Exception e) {
                    logger.warn("listener retry error", e);
                }
            }
            //执行任务
            try {
                job.pre(task);
                result = job.exec(task, execHistory.getExecReport());
                if (!StringUtils.isEmpty(execHistory.getExecReport().getMessage())) {
                    message.append(execHistory.getExecReport().getMessage()).append("\n");
                }
                execHistory.setExecMessage(message.toString());
            } catch (InterruptedException ex) {
                logger.error("task interrupted ,execId:{}", task.getExecId());
                //中断退出任务
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, ex);
                    listener.exceptionFinish(task, System.currentTimeMillis() - step, execHistory.getRetryTimes());
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
                execHistory.setCost(System.currentTimeMillis() - start);
                execHistory.setExecResult(ExecResultEnum.INTERRUPTED.getResult());
                return execHistory;
            } catch (Exception ex) {
                logger.error("task exec error, execId:{}", task.getExecId(), ex);
                message.append("【错误】").append(ex.getMessage()).append("\n");
                execHistory.setExecMessage(message.toString());
                execHistory.setCost(System.currentTimeMillis() - start);
                execHistory.setExecResult(ExecResultEnum.FAIL.getResult());
                //异常执行重试
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, ex);
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
                continue;
            }
            if (result) {
                if (execHistory.getRetryTimes() > 0) {
                    execHistory.setExecResult(ExecResultEnum.RETRY_SUCCESS.getResult());
                } else {
                    execHistory.setExecResult(ExecResultEnum.SUCCESS.getResult());
                }
                break;
            } else {
                execHistory.setExecResult(ExecResultEnum.FAIL.getResult());
                message.append("【").append(i).append("】").append("执行返回失败").append("\n");
                execHistory.setExecMessage(message.toString());
                //job 返回执行异常
                try {
                    listener.fail(task, System.currentTimeMillis() - step, i + 1, null);
                } catch (Exception e) {
                    logger.warn("listener fail error", e);
                }
            }
        }
        long cost = System.currentTimeMillis() - start;
        execHistory.setCost(cost);
        logger.info("task finished ,execId:{},execResult:{}", task.getExecId(), execHistory.getExecResult());
        //调用任务结束事件
        try {
            if (result) {
                listener.finish(task, cost, execHistory.getRetryTimes());
            } else {
                listener.exceptionFinish(task, cost, execHistory.getRetryTimes());
            }
        } catch (Exception e) {
            logger.warn("listener finish error", e);
        }
        return execHistory;
    }
}




