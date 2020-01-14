package cn.cenxt.task.annotations;

import cn.cenxt.task.configuration.CenxtTaskConfiguration;
import cn.cenxt.task.configuration.CenxtTaskViewConfiguration;
import cn.cenxt.task.properties.CenxtTaskProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 启用分布式任务调度n
 * <p>
 * 通过 @see CenxtTaskConfiguration 加载所有配置
 *
 * @author cpt725@qq.com
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({CenxtTaskConfiguration.class, CenxtTaskViewConfiguration.class})
@EnableConfigurationProperties({CenxtTaskProperties.class})
public @interface EnableCenxtTask {

}
