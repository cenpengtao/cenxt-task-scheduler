package cn.cenxt.task.jobs;

import cn.cenxt.task.annotations.TaskInfo;
import cn.cenxt.task.model.Task;

/**
 * 超时任务检验
 */
@TaskInfo("超时任务检验任务：扫描处于执行中的任务，判断是否超时，如果超时重置任务状态")

public class TaskExpireCheckJob implements CenxtJob {

    /**
     * 开始执行任务
     *
     * @param task 任务
     * @return
     */
    @Override
    public boolean exec(Task task) throws Exception {

        return false;
    }

}
