package cn.cenxt.task;

import java.io.Serializable;

/**
 * 任务
 */
public class Task implements Serializable {
    /**
     * 任务编号
     */
    private int id;
    /**
     * 任务名词
     */
    private String name;
    /**
     * 执行编号
     */
    private String execId;

    private int retryTimes;

    private int expire;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
