package cn.cenxt.task.model;

/**
 * 任务描述
 */
public class TaskDescription {

    private String name;
    private String cron;
    private int expire=10;
    private int retryTimes=0;

    private String description="";

    private String paramsDescription="{}";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParamsDescription() {
        return paramsDescription;
    }

    public void setParamsDescription(String paramsDescription) {
        this.paramsDescription = paramsDescription;
    }
}
