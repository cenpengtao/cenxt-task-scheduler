package cn.cenxt.task.enums;

/**
 * 执行状态
 */
public enum ExecResultEnum {
    /**
     * 待执行
     */
    WAITING(0),
    /**
     * 执行中
     */
    RUNNING(1),
    /**
     * 执行成功
     */
    SUCCESS(2),
    /**
     * 重试中
     */
    RETRYING(3),
    /**
     * 重试成功
     */
    RETRY_SUCCESS(4),
    /**
     * 执行失败
     */
    FAIL(5),
    /**
     * 超时中断
     */
    INTERRUPTED(6);

    ExecResultEnum(int result) {
        this.result = result;
    }

    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
