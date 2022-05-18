# SpringBoot分布式任务调度服务
支持任务分布式调度、重跑，以及UI界面控制
## 特性
- 分布式任务调度，避免多节点重复执行任务
- 支持任务重试
- 支持任务异常重跑
- 支持UI界面控制
## 一、快速使用
#### 1、引入依赖
```
<dependency>
    <groupId>cn.cenxt</groupId>
    <artifactId>cenxt-task-scheduler-core</artifactId>
    <version>1.0.3</version>
</dependency>
```
#### 2、启动类添加@EnableCenxtTask，启用任务调度服务
```
@SpringBootApplication
@EnableCenxtTask
public class CenxtTaskSchedulerDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CenxtTaskSchedulerDemoApplication.class, args);
	}
}
```
#### 3、编写任务代码，实现CenxtJob接口，并添加@TaskInfo描述任务信息（可选）
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
        //任务业务代码

        //任务数据采集（可选）
        execReport.incrSuccessCount(1);
        execReport.incrFailCount(1);
        //返回true表示任务成功执行，false为任务失败
        return true;
    }
}
```
#### 4、启动服务，打开控制页面，维护任务
http://localhost:8080/{应用上下文(可选)}/cenxt-task-view/index.html

demo地址:http://task.cenxt.cn/task-demo/cenxt-task-view/index.html

默认账户：admin/admin、guest/guest

## 二、进阶
#### 1、参数配置
```
#（可选）是否启用配置,缺省为true
cenxt.task.enabled=true
#（可选）任务扫描间隔单位ms，缺省3000，最小500
cenxt.task.scanInterval=3000
#（可选）任务执行线程数,缺省3，最小3
cenxt.task.thread=3
#（可选）每次获取待执行任务条数,缺省3，最小3，同时不能大于任务执行线程数
cenxt.task.fetchSize=3
#（可选）初始化任务表，如果启用，不存在任务表将自动创建，缺省true
cenxt.task.initTable=true
#（可选）任务表的前缀
cenxt.task.tableNamePrefix=

#（可选）是否启用控制界面，缺省true
cenxt.task.view.enabled=true
#（可选）管理员用户名，缺省admin
cenxt.task.view.adminUsername=admin
#（可选）管理员密码，缺省admin
cenxt.task.view.adminPassword=admin
#（可选）一般用户名，缺省normal
cenxt.task.view.normalUsername=normal
#（可选）一般密码，缺省normal
cenxt.task.view.normalPassword=normal
#（可选）游客用户名，缺省guest
cenxt.task.view.guestUsername=guest
#（可选）游客密码，缺省guest
cenxt.task.view.guestPassword=guest
#（可选）单个IP最大登录尝试次数，缺省5
cenxt.task.view.maxTryCount=3000
```
配置中心地址：http://config.cenxt.cn/
账号：demo/123456
#### 2、数据统计
在执行批处理任务过程中，设置如下代码，会将执行进度上传到服务端，可以在执行记录看到实时数据变化
```
//任务数据采集（可选）
//对应执行记录中成功记录数
execReport.incrSuccessCount(1);
//对应执行记录中失败记录数
execReport.incrFailCount(1);
```
#### 3、任务监听器
实现CenxtTaskListener接口，并将其注入到Ioc容器中，将会覆盖默认任务监听器，得到任务监听事件。
```
package cn.cenxt.task.listeners;

import cn.cenxt.task.model.Task;

/**
 * 任务监听器
 */
public interface CenxtTaskListener {

    /**
     * 任务开始
     *
     * @param task 任务
     */
    void begin(Task task);

    /**
     * 任务执行成果并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    void finish(Task task, long cost, int retryTimes);


    /**
     * 任务执行失败并结束
     *
     * @param task       任务
     * @param cost       耗时，单位ms
     * @param retryTimes 重试次数
     */
    void exceptionFinish(Task task, long cost, int retryTimes);

    /**
     * 任务执行失败
     *
     * @param task  任务
     * @param cost  单次执行耗时，单位ms
     * @param times 执行次数，从1开始
     * @param e     异常信息
     */
    void fail(Task task, long cost, int times, Exception e);

    /**
     * 任务重试
     *
     * @param task       任务
     * @param retryTimes 重试次数，从1开始
     */
    void retry(Task task, int retryTimes);
}

```
#### 4、自定义验证
实现CenxtSecurityService接口，并将其注入到Ioc容器中，将会覆盖默认身份验证服务，实现自定义验证（默认登陆界面将会失效）。
```
package cn.cenxt.task.service;

import cn.cenxt.task.enums.RoleEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全服务
 */
public interface CenxtSecurityService {

    /**
     * 获取用户名
     *
     * @param request 请求
     * @return 角色 如果为null，表示未登录
     */
    String getUserName(HttpServletRequest request);

    /**
     * 获取角色
     *
     * @param request 请求
     * @return 角色 如果为null，表示未登录
     */
    RoleEnum getRole(HttpServletRequest request);
}
```
## 三、设计说明
#### 1、业务流程
启动任务扫描线程->查询可执行任务列表->锁定任务->添加执行记录->执行任务->计算下次执行时间，并释放任务
#### 2、数据表设计（程序启动，如有权限会自动创建）
##### （1）任务表
````
CREATE TABLE `cenxt_task` (
	`id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
	`name` VARCHAR(100) NOT NULL COMMENT '任务名称',
	`description` VARCHAR(256) NOT NULL COMMENT '描述',
	`enabled` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '启用状态',
	`flag` TINYINT(2) NOT NULL DEFAULT '0' COMMENT '执行状态：0待执行 1执行中 2执行失败',
	`cron_str` VARCHAR(256) NOT NULL COMMENT '时间表达式',
	`expire` INT(11) NOT NULL COMMENT '超时时间，单位分钟，0为不超时',
	`retry_times` INT(11) NOT NULL COMMENT '重试次数',
	`params` VARCHAR(4096) NOT NULL DEFAULT '' COMMENT '参数',
	`exec_time` TIMESTAMP NULL DEFAULT NULL COMMENT '执行时间',
	`exec_ip` VARCHAR(50) NULL DEFAULT NULL COMMENT '执行机器IP',
	`next_time` TIMESTAMP NOT NULL COMMENT '下次执行时间',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
	`creator` VARCHAR(50) NOT NULL COMMENT '创建人',
	`updator` VARCHAR(50) NULL DEFAULT NULL COMMENT '更新人',
	PRIMARY KEY (`id`),
	INDEX `name` (`name`),
	INDEX `create_time` (`create_time`),
	INDEX `update_time` (`update_time`),
	INDEX `exec_time` (`exec_time`),
	INDEX `next_time` (`next_time`),
	INDEX `expire` (`expire`),
	INDEX `exec_ip` (`exec_ip`)
)
COMMENT='任务表'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
````
##### （2）执行记录表
````
CREATE TABLE `cenxt_exec_history` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
	`task_id` BIGINT(20) NOT NULL COMMENT '任务编号',
	`exec_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '执行编号',
	`exec_ip` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '执行机器IP',
	`exec_time` TIMESTAMP NOT NULL COMMENT '执行时间',
	`finish_time` TIMESTAMP NULL DEFAULT NULL COMMENT '结束时间',
	`cost` DOUBLE NOT NULL DEFAULT '0' COMMENT '执行耗时，单位ms',
	`exec_result` TINYINT(2) NOT NULL COMMENT '执行结果：0待执行 1执行中 2执行成功 3重试中 4重试成功 5执行失败 6超时中断 ',
	`retry_times` INT(11) NULL DEFAULT '0' COMMENT '重试次数',
	`success_count` BIGINT(20) NULL DEFAULT '0' COMMENT '成功记录数',
	`fail_count` BIGINT(20) NULL DEFAULT '0' COMMENT '失败记录数',
	`exec_message` VARCHAR(10240) NULL DEFAULT '' COMMENT '执行信息',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `exec_id` (`exec_id`),
	INDEX `exec_time` (`exec_time`),
	INDEX `finish_time` (`finish_time`),
	INDEX `task_id` (`task_id`),
	INDEX `cost` (`cost`),
	INDEX `exec_ip` (`exec_ip`),
	INDEX `exec_result` (`exec_result`)
)
COMMENT='执行记录表'
COLLATE='utf8mb4_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=1;
````
#### 3、查询可执行任务列表
````
SELECT * FROM cenxt_task WHERE 
##任务是启用状态
enabled=1 
AND (
	##任务待执行且下次执行时间小于当前时间
	(flag=0 AND next_time<NOW()) 
	##任务为执行中，且任务已超时执行
	OR (flag=1 AND exec_time<DATE_SUB(NOW(),interval `expire`+1 MINUTE))
	OR (
		##执行失败
		flag=2 
		AND (
			##如果上次执行机器为当前机器，执行时间延后1分钟
			(exec_ip=? AND next_time<DATE_SUB(NOW(),interval 1 MINUTE))
			##如果上次执行机器不是当前，到时间自动执行
			OR (exec_ip<>? AND next_time<NOW())
		)
	)
) LIMIT ?
````
#### 4、锁定任务
````
##设置执行状态为执行中，更新执行时间和机器IP
UPDATE cenxt_task SET flag=1,exec_time=NOW(),exec_ip=? WHERE 
##任务编号
id=? 
##是否启用
AND enabled=1 
##执行时间和查询的结果一致，或者执行时间为空（任务新创建执行时间为空）
AND (exec_time=? OR exec_time IS NULL)
````
#### 5、释放任务
````
##更新执行状态和下次执行时间
UPDATE cenxt_task SET flag=?,next_time=? WHERE 
##任务编号
id=? 
##任务状态为执行中
AND flag=1
````
