package cn.cenxt.task.configuration;

import cn.cenxt.task.filter.CenxtTaskFilter;
import cn.cenxt.task.service.CenxtSecurityService;
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
    @ConditionalOnMissingBean(CenxtSecurityService.class)
    public CenxtSecurityService cenxtSecurityService() {
        return new DefaultCenxtSecurityService();
    }

    @Bean
    @Order
    @ConditionalOnProperty(
            name = {"cenxt.task.view.needLogin"},
            havingValue = "true",
            matchIfMissing = true
    )
    public FilterRegistrationBean<CenxtTaskFilter> cenxtTaskFilter(CenxtSecurityService cenxtSecurityService) {
        FilterRegistrationBean<CenxtTaskFilter> filterRegistrationBean
                = new FilterRegistrationBean<>(new CenxtTaskFilter(cenxtSecurityService));
        filterRegistrationBean.addUrlPatterns("/cenxt-task-view/*");
        return filterRegistrationBean;
    }


}
