package cn.cenxt.task.service;

import cn.cenxt.task.enums.RoleEnum;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全服务
 */
public interface CenxtSecurityService {

    /**
     * 获取用户名
     *
     * @param request 请求
     * @return 角色 如果为null，表示未登录
     */
    String getUserName(HttpServletRequest request);

    /**
     * 获取角色
     *
     * @param request 请求
     * @return 角色 如果为null，表示未登录
     */
    RoleEnum getRole(HttpServletRequest request);
}
