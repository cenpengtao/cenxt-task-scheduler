package cn.cenxt.task.jobs;

import cn.cenxt.task.model.ExecReport;
import cn.cenxt.task.model.Task;

/**
 * 任务接口
 */
public interface CenxtJob {

    /**
     * 任务执行之前
     *
     * @param task 任务
     */
    default void pre(Task task) {
    }

    ;

    /**
     * 开始执行任务
     *
     * @param task       任务
     * @param execReport 执行状态
     * @return 执行结果
     */
    boolean exec(final Task task, final ExecReport execReport) throws Exception;

}
