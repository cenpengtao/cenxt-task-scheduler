package cn.cenxt.task.demo;

import cn.cenxt.task.annotations.EnableCenxtTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCenxtTask
public class CenxtTaskSchedulerDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(CenxtTaskSchedulerDemoApplication.class, args);
	}

}
