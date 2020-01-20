package cn.cenxt.task.configuration;

import cn.cenxt.task.filter.CenxtTaskFilter;
import cn.cenxt.task.properties.CenxtTaskProperties;
import cn.cenxt.task.service.DefaultCenxtSecurityService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

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
@ComponentScan(basePackages = "cn.cenxt.task.controller")
public class CenxtTaskViewConfiguration {

    @Bean
    @Order
    @ConditionalOnProperty(
            name = {"cenxt.task.view.needLogin"},
            havingValue = "true",
            matchIfMissing = true
    )
    public FilterRegistrationBean<CenxtTaskFilter> cenxtTaskFilter(CenxtTaskProperties taskProperties) {
        FilterRegistrationBean<CenxtTaskFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CenxtTaskFilter(taskProperties));
        filterRegistrationBean.addUrlPatterns(taskProperties.getView().getContentPath() + "/*");
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(DefaultCenxtSecurityService.class)
    public DefaultCenxtSecurityService cenxtSecurityService(CenxtTaskProperties taskProperties) {
        return new DefaultCenxtSecurityService(taskProperties);
    }
}
