# SpringBoot分布式任务调度服务
支持任务分布式调度、重跑，以及UI界面控制
## 特性
- 分布式任务调度，避免多节点重复执行任务
- 支持任务重试
- 支持任务异常重跑
- 支持UI界面控制
##快速使用
####1、引入依赖
```
<dependency>
    <groupId>cn.cenxt</groupId>
    <artifactId>cenxt-task-scheduler-core</artifactId>
    <version>1.0.0</version>
</dependency>
```
####2、启动类添加@EnableCenxtTask，启用任务调度服务
```
@SpringBootApplication
@EnableCenxtTask
public class CenxtTaskSchedulerDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CenxtTaskSchedulerDemoApplication.class, args);
	}
}
```
####3、编写任务代码，实现CenxtJob接口，并添加@TaskInfo描述任务信息（可选）
```
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
        execReport.incrSuccessCount(1);
        execReport.incrFailCount(1);
        return true;
    }
}
```
####4、启动服务，打开控制页面，维护任务
http://localhost:8080/{应用上下文(可选)}/cenxt-task-view/index.html
demo地址:http://cenxt.cn/task-demo/cenxt-task-view/index.html

默认账户：admin/admin、guest/guest

##进阶
####1、参数配置

####2、数据统计

####3、任务监听器

####4、自定义验证