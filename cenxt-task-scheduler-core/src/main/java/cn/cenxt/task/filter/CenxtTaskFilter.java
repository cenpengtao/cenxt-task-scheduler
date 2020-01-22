package cn.cenxt.task.filter;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.enums.RoleEnum;
import cn.cenxt.task.properties.CenxtTaskProperties;
import cn.cenxt.task.service.CenxtSecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制台过滤器
 */
public class CenxtTaskFilter implements Filter {


    private CenxtTaskProperties taskProperties;

    private CenxtSecurityService securityService;

    public CenxtTaskFilter(CenxtTaskProperties taskProperties, CenxtSecurityService securityService) {
        this.taskProperties = taskProperties;
        this.securityService = securityService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String url = request.getRequestURI();

        //排除登录地址
        String[] urls = {
                taskProperties.getView().getContentPath() + "/api/login",
                taskProperties.getView().getContentPath() + "/#/login",
                taskProperties.getView().getContentPath() + "/#/",
                taskProperties.getView().getContentPath() + "/",
                taskProperties.getView().getContentPath() + "/index.html",
        };
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String str : urls) {
            if (pathMatcher.match(str, url)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String userName = securityService.getUserName(request);
        RoleEnum role = securityService.getRole(request);
        if (StringUtils.isEmpty(userName) || role == null) {
            //标记为未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().flush();
            return;
        }
        if (pathMatcher.match(url, taskProperties.getView().getContentPath() + "/api/admin/**")) {

            if (role.getRole() < 1) {
                //标记为无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().flush();
                return;
            }
        }
        //如是外部提供登录验证需重新设置用户名
        request.getSession().setAttribute(Constants.SESSION_USERNAME, userName);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
