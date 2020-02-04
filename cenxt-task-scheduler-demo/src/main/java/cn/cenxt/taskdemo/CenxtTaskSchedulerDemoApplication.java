package cn.cenxt.taskdemo;

import cn.cenxt.task.annotations.EnableCenxtTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCenxtTask
@ComponentScan(basePackages = "cn.cenxt.taskdemo")
public class CenxtTaskSchedulerDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CenxtTaskSchedulerDemoApplication.class, args);
	}
}
