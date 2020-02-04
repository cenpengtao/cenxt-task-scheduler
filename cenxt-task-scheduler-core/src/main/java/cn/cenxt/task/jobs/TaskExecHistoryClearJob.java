package cn.cenxt.task.jobs;

import cn.cenxt.task.annotations.TaskInfo;
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
@TaskInfo(description = "删除任务执行记录",paramsDescription = "{\n" +
        "  \"?beforeDay\": \"删除多少天前的记录，默认3\",\n" +
        "  \"beforeDay\": 5,\n" +
        "  \"?size\": \"删除的行数，默认100\",\n" +
        "  \"size\": \"100\"\n" +
        "}")
public class TaskExecHistoryClearJob implements CenxtJob {

    private static Logger logger = LoggerFactory.getLogger(TaskExecHistoryClearJob.class);

    /**
     * 删除多少天前的记录
     */
    private static final String PARAM_BEFORE_DAY = "beforeDay";
    /**
     * 删除的行数
     */
    private static final String PARAM_SIZE = "size";

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
        int size = task.getParam(PARAM_SIZE, Integer.class, 100);
        logger.info("beforeDay:{},size:{}",beforeDay,size);
        Date nowTime = cenxtTaskService.getNowTime();
        Date date = new Date(nowTime.getTime() - beforeDay * 24 * 60 * 60 * 1000);
        int count = cenxtTaskService.deleteExecHistory(date, size);
        execReport.incrSuccessCount(count);
        logger.info("delete success,count:{}", count);
        return true;
    }
}
