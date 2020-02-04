package cn.cenxt.taskdemo;

import cn.cenxt.task.annotations.TaskInfo;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.model.ExecReport;
import cn.cenxt.task.model.Task;
import org.springframework.stereotype.Component;

@Component
@TaskInfo(description = "测试任务",paramsDescription = "{}",cron = "0 */1 * * * ?")
public class TestJob implements CenxtJob {
    /**
     * 开始执行任务
     * !!!不要捕获InterruptedException异常，否则超时进程无法停止
     *
     * @param task       任务
     * @param execReport 执行状态
     * @return 执行结果
     */
    @Override
    public boolean exec(Task task, ExecReport execReport) throws Exception {

        //任务业务代码

        //任务数据采集（可选）
        execReport.incrSuccessCount(1);
        execReport.incrFailCount(1);
        return true;
    }
}
