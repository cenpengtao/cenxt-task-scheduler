package cn.cenxt.task.model;

import java.util.Date;

/**
 * 执行记录模型
 */
public class ExecHistory {

    /**
     * 编号
     */
    private long id;
    /**
     * 任务编号
     */
    private int taskId;
    /**
     * 执行编号
     */
    private String execId;
    /**
     * 执行ip
     */
    private String execIp;
    /**
     * 执行时间
     */
    private Date execTime;
    /**
     * 结束时间
     */
    private Date finishTime;
    /**
     * 耗时
     */
    private long cost;
    /**
     * 执行结果
     */
    private int execResult;
    /**
     * 重试次数
     */
    private int retryTimes;

    private ExecReport execReport = new ExecReport();
    /**
     * 错误信息
     */
    private String execMessage = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getExecIp() {
        return execIp;
    }

    public void setExecIp(String execIp) {
        this.execIp = execIp;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public ExecReport getExecReport() {
        return execReport;
    }

    public void setExecReport(ExecReport execReport) {
        this.execReport = execReport;
    }

    public int getExecResult() {
        return execResult;
    }

    public void setExecResult(int execResult) {
        this.execResult = execResult;
    }

    public String getExecMessage() {
        return execMessage;
    }

    public void setExecMessage(String execMessage) {
        this.execMessage = execMessage;
    }
}
