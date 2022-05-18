package cn.cenxt.task.scheduler;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.enums.ExecResultEnum;
import cn.cenxt.task.enums.TaskStatusEnum;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.properties.CenxtTaskProperties;
import cn.cenxt.task.service.CenxtTaskService;
import cn.cenxt.task.utils.CronAnalysisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务调度器
 */
public class CenxtTaskScheduler implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CenxtTaskScheduler.class);

    private static ThreadPoolExecutor executor;

    @Autowired
    private CenxtTaskProperties taskProperties;

    @Autowired
    private CenxtTaskService cenxtTaskService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CenxtTaskListener cenxtTaskListener;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String tableNamePrefix = taskProperties.getTableNamePrefix();
        if (!StringUtils.isEmpty(tableNamePrefix)) {
            tableNamePrefix = tableNamePrefix.trim();
            if (!Constants.TABLE_NAME_TASK.startsWith(tableNamePrefix)) {
                Constants.TABLE_NAME_TASK = tableNamePrefix + Constants.TABLE_NAME_TASK;
            }
            if (!Constants.TABLE_NAME_EXEC_HISTORY.startsWith(tableNamePrefix)) {
                Constants.TABLE_NAME_EXEC_HISTORY = tableNamePrefix + Constants.TABLE_NAME_EXEC_HISTORY;
            }
        }
        if (taskProperties.isInitTable()) {
            //初始化任务表
            try {
                cenxtTaskService.initTable();
            } catch (Exception e) {
                logger.error("initTable error", e);
                System.exit(1);
                return;
            }
        }
        int interval = Math.max(taskProperties.getScanInterval(), Constants.MIN_INTERVAL);
        final int[] fetchSize = {Math.max(taskProperties.getFetchSize(), Constants.DEFAULT_FETCH_SIZE)};
        int poolSize = Math.max(taskProperties.getThread(), Constants.DEFAULT_THREAD_SIZE);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        logger.info("success create executor with size {}", poolSize);
        new Thread(CenxtTaskScheduler.class.getSimpleName()) {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(interval);
                    } catch (Exception e) {
                        logger.warn("CenxtTaskScheduler sleep error", e);
                    }
                    //开始扫描任务
                    try {
                        int size = Math.min(fetchSize[0], (executor.getCorePoolSize() - executor.getActiveCount()));
                        logger.debug("now available pool size:{}", size);
                        printPool(executor);
                        if (size < 1) {
                            continue;
                        }
                        //获取待执行任务列表
                        List<Task> waitExecTask = cenxtTaskService.getWaitExecTask(size);
                        logger.debug("get tasks size:{}", waitExecTask.size());
                        for (Task task : waitExecTask) {
                            try {
                                //获取执行任务
                                CenxtJob job = applicationContext.getBean(task.getName(), CenxtJob.class);

                                //判断任务表达式
                                if (CronAnalysisUtil.getNextTime(task.getCronStr(), new Date()) == null) {
                                    continue;
                                }

                                logger.debug("try to lock task:{}", task);
                                if (!cenxtTaskService.lockTask(task)) {
                                    logger.debug("fail to lock task:{}", task);
                                    continue;
                                }

                                //设置执行编号
                                task.setExecId(UUID.randomUUID().toString());
                                logger.info("success lock task:{}", task);
                                task.setExecTime(cenxtTaskService.getNowTime());
                                //包装执行器
                                ExecWrapper execWrapper = new ExecWrapper(task, job, cenxtTaskListener, cenxtTaskService);
                                //添加执行记录
                                cenxtTaskService.saveExecHistory(task, null, ExecResultEnum.WAITING, null);
                                //执行任务
                                executor.execute(execWrapper);
                                logger.info("task submit ,execId:{}", task.getExecId());
                            } catch (BeansException e) {
                                logger.warn("not found task. task:{}", task.getName());
                                //添加执行记录
                                cenxtTaskService.saveExecHistory(task, cenxtTaskService.getNowTime(), ExecResultEnum.FAIL, "not found task");
                                cenxtTaskService.failAndDisableTask(task.getId());

                            } catch (RejectedExecutionException e) {
                                logger.error("CenxtTaskScheduler exec error,task:{},execId:{}", task.getName(), task.getExecId(), e);
                                //添加执行记录
                                cenxtTaskService.saveExecHistory(task, cenxtTaskService.getNowTime(), ExecResultEnum.FAIL, "pool reject");
                                //释放任务
                                cenxtTaskService.releaseTask(task, TaskStatusEnum.FAIL);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("CenxtTaskScheduler Error", e);
                    }
                }
            }
        }.start();
    }

    private void printPool(ThreadPoolExecutor executor) {
        logger.debug(" ActiveCount: {} poolSize:{} queueSize:{} taskCount:{}"
                , executor.getActiveCount(),
                executor.getPoolSize(),
                executor.getQueue().size(),
                executor.getTaskCount());
    }
}
