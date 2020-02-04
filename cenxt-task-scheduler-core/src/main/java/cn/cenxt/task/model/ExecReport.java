package cn.cenxt.task.model;

/**
 * 执行报告
 */
public class ExecReport {

    /**
     * 成功记录数
     */
    private long successCount;
    /**
     * 失败记录数
     */
    private long failCount;
    /**
     * 信息
     */
    private String message;

    public ExecReport() {

    }

    public ExecReport(long successCount, long failCount) {
        this.successCount = successCount;
        this.failCount = failCount;
    }

    /**
     * 自增成功记录数
     *
     * @param count 记录数
     * @return 自增后记录数
     */
    public synchronized long incrSuccessCount(long count) {
        successCount += count;
        return successCount;
    }

    public long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    /**
     * 自增失败记录数
     *
     * @param count 记录数
     * @return 自增后记录数
     */
    public synchronized long incrFailCount(long count) {
        failCount += count;
        return failCount;
    }


    public long getFailCount() {
        return failCount;
    }

    public void setFailCount(long failCount) {
        this.failCount = failCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
