package cn.cenxt.task.enums;

/**
 * 任务状态
 */
public enum TaskStatusEnum {
    /**
     * 待执行
     */
    WAITING(0),
    /**
     * 执行中
     */
    RUNNING(1),
    /**
     * 执行失败
     */
    FAIL(2);

    TaskStatusEnum(int status) {
        this.status = status;
    }

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
