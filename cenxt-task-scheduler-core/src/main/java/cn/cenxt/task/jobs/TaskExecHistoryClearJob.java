package cn.cenxt.task.jobs;

import cn.cenxt.task.model.ExecReport;
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
    private static final String SIZE = "size";

    private static int i = 0;
    @Autowired
    private CenxtTaskService cenxtTaskService;

    /**
     * 开始执行任务
     *
     * @param task 任务
     * @return
     */
    @Override
    public boolean exec(Task task, final ExecReport execReport) throws Exception {
        logger.info(task.toString());
        int beforeDay = task.getParam(PARAM_BEFORE_DAY, Integer.class, 3);
        int size = task.getParam(SIZE, Integer.class, 100);
        Date nowTime = cenxtTaskService.getNowTime();
        Date date = new Date(nowTime.getTime() - beforeDay * 24 * 60 * 60 * 100);
        int count = cenxtTaskService.deleteExecHistory(date, size);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            execReport.incrSuccessCount(2);
            execReport.incrFailCount(1);
        }
        logger.info("delete success,count:{}", count);
        i++;
        if (i == 4) {
            return true;
        } else {
            return false;
        }
    }
}
