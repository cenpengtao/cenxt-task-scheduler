package cn.cenxt.task.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

/**
 * 控制界面配置
 *
 * @author cpt725@qq.com
 */
@ConditionalOnWebApplication
@ConditionalOnProperty(
        name = {"cenxt.task.view.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
@AutoConfigureAfter(JdbcTemplateAutoConfiguration.class)
public class CenxtTaskViewConfiguration {

}
