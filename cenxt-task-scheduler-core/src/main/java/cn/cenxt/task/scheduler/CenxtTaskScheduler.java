package cn.cenxt.task.scheduler;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.properties.CenxtTaskProperties;
import cn.cenxt.task.service.CenxtTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
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
        new Thread(CenxtTaskScheduler.class.getSimpleName()) {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        logger.warn("CenxtTaskScheduler InterruptedException", e);
                    }
                    //开始扫描任务
                    try {
                        fetchSize[0] = Math.min(fetchSize[0], (executor.getCorePoolSize() - executor.getActiveCount()));
                        logger.debug("now available pool size:{}", fetchSize[0]);
                        if (fetchSize[0] < 1) {
                            continue;
                        }
                        //获取待执行任务列表
                        List<Task> waitExecTask = cenxtTaskService.getWaitExecTask(fetchSize[0]);
                        logger.debug("get tasks size:{}", waitExecTask.size());
                        for (Task task : waitExecTask) {
                            logger.debug("try to lock task:{},id:{}", task.getName(), task.getId());
                            if (!cenxtTaskService.lockTask(task)) {
                                logger.debug("fail to lock task:{},id:{}", task.getName(), task.getId());
                                continue;
                            }
                            logger.debug("success lock task:{},id:{}", task.getName(), task.getId());
                            try {
                                //设置执行编号
                                task.setExecId(UUID.randomUUID().toString());
                                task.setExecTime(cenxtTaskService.getNowTime());
                                //获取执行任务
                                CenxtJob job = applicationContext.getBean(task.getName(), CenxtJob.class);
                                //包装执行器
                                ExecWrapper execWrapper = new ExecWrapper(task, job, cenxtTaskListener, cenxtTaskService);
                                //执行任务
                                executor.execute(execWrapper);
                            } catch (BeansException e) {
                                logger.error("not found task. task:{}", task.getName());
                            } catch (Exception e) {
                                logger.error("CenxtTaskScheduler exec error,task:{},execId:{}", task.getName(), task.getExecId(), e);
                                try {
                                    //添加执行记录
                                    cenxtTaskService.insertExecHistory(task.getId(), task.getExecId(), task.getExecTime(),
                                            cenxtTaskService.getNowTime(), 2, e.getMessage());
                                } catch (Exception ex) {
                                    logger.error("insertExecHistory error", ex);
                                }
                                try {
                                    //释放任务
                                    cenxtTaskService.releaseTask(task, 2);
                                } catch (Exception ex) {
                                    logger.error("releaseTask error", ex);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("CenxtTaskScheduler Error", e);
                    }
                }
            }
        }.start();
    }
}
