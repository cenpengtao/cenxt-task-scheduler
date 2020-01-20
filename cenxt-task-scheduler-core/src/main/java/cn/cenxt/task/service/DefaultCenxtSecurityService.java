package cn.cenxt.task.service;

import cn.cenxt.task.enums.RoleEnum;
import cn.cenxt.task.properties.CenxtTaskProperties;
import org.springframework.util.StringUtils;

/**
 * 默认安全服务
 */
public class DefaultCenxtSecurityService {

    private CenxtTaskProperties properties;

    public DefaultCenxtSecurityService(CenxtTaskProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取角色
     *
     * @param username 用户名
     * @param password 密码
     * @return 角色 如果为null，表示未登录
     */
    public RoleEnum getRole(String username, String password) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }
        if (username.equals(properties.getView().getAdminUsername()) && password.equals(properties.getView().getAdminPassword())) {
            return RoleEnum.ADMIN;
        }
        if (username.equals(properties.getView().getGuestUsername()) && password.equals(properties.getView().getGuestPassword())) {
            return RoleEnum.GUEST;
        }
        return null;
    }
}
