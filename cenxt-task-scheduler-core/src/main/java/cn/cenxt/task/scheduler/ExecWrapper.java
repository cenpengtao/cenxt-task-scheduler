package cn.cenxt.task.scheduler;

import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.service.CenxtTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行包装类
 */
public class ExecWrapper implements Runnable {

    private final Logger log = LoggerFactory.getLogger(ExecWrapper.class);
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
        Thread thread = new ExecThread(task, job, listener, cenxtTaskService);
        try {
            log.info("task thread begin ,execId:{}", task.getExecId());
            thread.start();
            //超时检查
            if (task.getExpire() > 0) {
                thread.join(task.getExpire() * 60 * 1000);
                //超时后停止线程
                thread.interrupt();
            } else {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
