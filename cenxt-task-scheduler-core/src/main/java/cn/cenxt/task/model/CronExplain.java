package cn.cenxt.task.model;

/**
 * cron解析
 */
public class CronExplain {

    /**
     * cron表达式
     */
    private String cronStr;

    /**
     * 执行时间条数
     */
    private int size = 3;

    public String getCronStr() {
        return cronStr;
    }

    public void setCronStr(String cronStr) {
        this.cronStr = cronStr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
