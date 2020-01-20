package cn.cenxt.task.filter;

import cn.cenxt.task.properties.CenxtTaskProperties;
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

    public CenxtTaskFilter(CenxtTaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String url = request.getRequestURI();

        //排除登录地址
        String[] urls = {
                taskProperties.getView().getContentPath() + "/api/login",
                taskProperties.getView().getContentPath() + "/#/login"};
        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String str : urls) {
            if (pathMatcher.match(str, url)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (StringUtils.isEmpty(request.getSession().getAttribute("CENXT_TASK_USERNAME"))) {
            //标记为未登录
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().flush();
            return;
        }
        if (pathMatcher.match(url, "/**/task-view/api/admin/**")) {
            Object roleObject = request.getSession().getAttribute("CENXT_TASK_USER_ROLE");
            if (roleObject == null || (Integer) roleObject < 1) {
                //标记为无权限
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
