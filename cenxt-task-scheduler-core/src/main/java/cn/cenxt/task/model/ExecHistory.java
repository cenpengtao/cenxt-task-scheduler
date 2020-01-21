package cn.cenxt.task.model;

import java.util.Date;

/**
 * 执行历史模型
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
    private double cost;
    /**
     * 执行结果
     */
    private int execResult;
    /**
     * 错误信息
     */
    private String errorMessage;


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

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getExecResult() {
        return execResult;
    }

    public void setExecResult(int execResult) {
        this.execResult = execResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
