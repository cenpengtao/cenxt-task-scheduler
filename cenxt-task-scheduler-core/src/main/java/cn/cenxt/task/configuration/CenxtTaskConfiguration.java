package cn.cenxt.task.configuration;

import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.listeners.CenxtTaskListener;
import cn.cenxt.task.listeners.DefaultTaskListener;
import cn.cenxt.task.scheduler.CenxtTaskScheduler;
import cn.cenxt.task.service.CenxtTaskService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 任务配置
 *
 * @author cpt725@qq.com
 */
@Import({CenxtTaskScheduler.class})
@ConditionalOnProperty(
        name = {"cenxt.task.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@ComponentScan(basePackageClasses = {CenxtJob.class})
public class CenxtTaskConfiguration {

    /**
     * 创建任务监听器
     */
    @ConditionalOnMissingBean(CenxtTaskListener.class)
    @Bean
    public CenxtTaskListener taskListener() {
        return new DefaultTaskListener();
    }

    /**
     * 创建任务服务
     */
    @Bean
    @ConditionalOnMissingBean(CenxtTaskService.class)
    public CenxtTaskService cenxtTaskService(JdbcTemplate jdbcTemplate) {
        return new CenxtTaskService(jdbcTemplate);
    }
}
