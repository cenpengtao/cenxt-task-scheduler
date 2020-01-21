package cn.cenxt.task.service;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.enums.RoleEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认安全服务
 */
public class DefaultCenxtSecurityService implements CenxtSecurityService {

    /**
     * 获取用户名
     *
     * @param request 请求
     * @return 角色 如果为null，表示未登录
     */
    @Override
    public String getUserName(HttpServletRequest request) {
        Object username = request.getSession().getAttribute(Constants.SESSION_USERNAME);
        if (username == null) {
            return null;
        }
        return (String) username;
    }

    /**
     * 获取角色
     *
     * @return 角色 如果为null，表示未登录
     */
    @Override
    public RoleEnum getRole(HttpServletRequest request) {
        Object role = request.getSession().getAttribute(Constants.SESSION_ROLE);
        if (role == null) {
            return null;
        }
        return (RoleEnum) role;
    }
}
