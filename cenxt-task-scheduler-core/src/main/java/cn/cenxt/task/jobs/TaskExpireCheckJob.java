package cn.cenxt.task.jobs;

import cn.cenxt.task.annotations.CenxtTask;
import cn.cenxt.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 超时任务检验
 */
@CenxtTask("超时任务检验任务：扫描处于执行中的任务，判断是否超时，如果超时重置任务状态")
@Component
public class TaskExpireCheckJob implements CenxtJob {

    private static Logger logger= LoggerFactory.getLogger(TaskExpireCheckJob.class);
    /**
     * 开始执行任务
     *
     * @param task 任务
     * @return
     */
    @Override
    public boolean exec(Task task) throws Exception {
        logger.info(task.toString());
        return false;
    }

}
