package cn.cenxt.task.listeners;

import cn.cenxt.task.model.Task;

/**
 * 任务监听器
 */
public interface CenxtTaskListener {

    /**
     * 任务开始
     *
     * @param task 任务
     */
    void begin(Task task);

    /**
     * 任务执行成果并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    void finish(Task task, long cost, int retryTimes);


    /**
     * 任务执行失败并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    void exceptionFinish(Task task, long cost, int retryTimes);

    /**
     * 任务执行失败
     *
     * @param task  任务
     * @param cost  单次执行耗时，单位ms
     * @param times 执行次数，从1开始
     * @param e     异常信息
     */
    void fail(Task task, long cost, int times, Exception e);

    /**
     * 任务重试
     *
     * @param task       任务
     * @param retryTimes 重试次数，从1开始
     */
    void retry(Task task, int retryTimes);
}
