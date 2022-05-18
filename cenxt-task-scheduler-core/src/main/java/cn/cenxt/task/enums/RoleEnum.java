package cn.cenxt.task.enums;

/**
 * 角色枚举
 */
public enum RoleEnum {
    /**
     * 访客
     */
    GUEST(0, "GUEST"),
    /**
     * 一般用户
     */
    NORMAL(1, "NORMAL"),
    /**
     * 管理员
     */
    ADMIN(2, "ADMIN");

    RoleEnum(int role, String name) {
        this.role = role;
        this.name = name;
    }

    private int role;

    private String name;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }
}
