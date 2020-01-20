package cn.cenxt.task.jobs;

import cn.cenxt.task.annotations.CenxtTask;
import cn.cenxt.task.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 任务执行历史清理
 */
@CenxtTask("任务执行历史清理")
@Component
class TaskExecHistoryClearJob implements CenxtJob {

    private static Logger logger = LoggerFactory.getLogger(TaskExecHistoryClearJob.class);

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
