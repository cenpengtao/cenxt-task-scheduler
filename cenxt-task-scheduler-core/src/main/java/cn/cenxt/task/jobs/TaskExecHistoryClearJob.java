package cn.cenxt.task.jobs;

import cn.cenxt.task.model.Task;
import cn.cenxt.task.service.CenxtTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 任务执行历史清理
 */
@Component
public class TaskExecHistoryClearJob implements CenxtJob {

    private static Logger logger = LoggerFactory.getLogger(TaskExecHistoryClearJob.class);

    private static final String PARAM_BEFORE_DAY = "beforeDay";

    @Autowired
    private CenxtTaskService cenxtTaskService;

    /**
     * 开始执行任务
     *
     * @param task 任务
     * @return
     */
    @Override
    public boolean exec(Task task) throws Exception {
        logger.info(task.toString());
        Integer beforeDay = task.getParam(PARAM_BEFORE_DAY, Integer.class, 3);
        Date nowTime = cenxtTaskService.getNowTime();
        Date date = new Date(nowTime.getTime() - beforeDay * 24 * 60 * 60 * 100);
        int count = cenxtTaskService.deleteExecHistory(date);
        logger.info("delete success,count:{}", count);
        return true;
    }


}
