package cn.cenxt.task.listeners;

import cn.cenxt.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认任务监听器
 */
public class DefaultTaskListener implements CenxtTaskListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTaskListener.class);

    /**
     * 任务开始
     *
     * @param task 任务
     */
    @Override
    public void begin(Task task) {
        logger.info("task begin. name:{},execId:{}", task.getName(), task.getExecId());
    }

    /**
     * 任务执行成果并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    @Override
    public void finish(Task task, long cost, int retryTimes) {
        if (retryTimes == 0) {
            logger.info("task finished. name:{},execId:{},cost:{}ms", task.getName(), task.getExecId(), cost);
        } else {
            logger.info("task finished after {} times retry. name:{},execId:{},cost:{}ms", retryTimes, task.getName(), task.getExecId(), cost);
        }
    }

    /**
     * 任务执行失败并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    @Override
    public void exceptionFinish(Task task, long cost, int retryTimes) {
        logger.error("task exception finish after {} times retry. name:{},execId:{},cost:{}ms", retryTimes, task.getName(), task.getExecId(), cost);

    }

    /**
     * 任务执行失败
     *
     * @param task  任务
     * @param cost  单词执行耗时，单位ms
     * @param times 执行次数，从1开始
     * @param e     异常信息
     */
    @Override
    public void fail(Task task, long cost, int times, Exception e) {
        logger.error("task exec fail. name:{},execId:{},cost:{}ms", task.getName(), task.getExecId(), cost);
    }

    /**
     * 任务重试
     *
     * @param task       任务
     * @param retryTimes 重试次数，从1开始
     */
    @Override
    public void retry(Task task, int retryTimes) {
        logger.warn("task exec fail will retry. name:{},execId:{},retryTimes:{}", task.getName(), task.getExecId(), retryTimes);
    }
}
