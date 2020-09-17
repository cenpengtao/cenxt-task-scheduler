package cn.cenxt.task.scheduler;

import cn.cenxt.task.enums.ExecResultEnum;
import cn.cenxt.task.enums.TaskStatusEnum;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.ExecHistory;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.service.CenxtTaskService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 执行包装类
 */
public class ExecWrapper implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(ExecWrapper.class);
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
     * 任务服务
     */
    private CenxtTaskService cenxtTaskService;

    public ExecWrapper(Task task, CenxtJob job, CenxtTaskListener listener, CenxtTaskService cenxtTaskService) {
        this.task = task;
        this.job = job;
        this.listener = listener;
        this.cenxtTaskService = cenxtTaskService;
    }

    @Override
    public void run() {
        cenxtTaskService.saveExecHistory(task, null, ExecResultEnum.RUNNING, null);
        //传入执行记录
        ExecHistory execHistory = new ExecHistory();
        execHistory.setExecResult(ExecResultEnum.RUNNING.getResult());
        FutureTask<ExecHistory> futureTask = new FutureTask<>(
                //复制task
                new ExecCallable(JSON.parseObject(JSON.toJSONString(task), Task.class),
                        job, listener, execHistory));
        Thread thread = new Thread(futureTask);
        thread.setName(task.getExecId());

        logger.info("task thread begin ,execId:{}", task.getExecId());
        thread.start();
        long start = System.currentTimeMillis();
        while (!futureTask.isDone()) {
            long cost = System.currentTimeMillis() - start;
            execHistory.setCost(cost);
            //超时检查
            if (task.getExpire() > 0) {

                if (cost > task.getExpire() * 60 * 1000) {
                    try {
                        logger.error("task expire, will call cancel,execId:{}", task.getExecId());
                        //超时后停止线程
                        futureTask.cancel(true);
                    } catch (Exception e) {
                        logger.error("cancel error", e);
                    }
                    execHistory.setExecResult(ExecResultEnum.INTERRUPTED.getResult());
                    execHistory.setExecMessage(execHistory.getExecMessage() + "【错误】在" + cost + "毫秒之后,任务被中断");
                }
            }
            if(execHistory.getExecResult() != ExecResultEnum.RUNNING.getResult()
                    && execHistory.getExecResult() != ExecResultEnum.RETRYING.getResult()){
                break;
            }
            cenxtTaskService.saveExecHistory(task, null, execHistory);
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
        try {
            execHistory = futureTask.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("futureTask get error", e);
        }
        cenxtTaskService.saveExecHistory(task, cenxtTaskService.getNowTime(), execHistory);
        if (execHistory.getExecResult() == ExecResultEnum.SUCCESS.getResult()
                || execHistory.getExecResult() == ExecResultEnum.RETRY_SUCCESS.getResult()) {
            //释放任务
            cenxtTaskService.releaseTask(task, TaskStatusEnum.WAITING);
        } else {
            //释放任务
            cenxtTaskService.releaseTask(task, TaskStatusEnum.FAIL);
        }


    }
}
