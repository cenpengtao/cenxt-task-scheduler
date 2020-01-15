package cn.cenxt.task.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
     * 任务描述
     */
    private String description;
    /**
     * 执行编号
     */
    private String execId;

    /**
     * 重试次数
     */
    private int retryTimes;

    /**
     * 超时时间，单位分钟
     */
    private int expire;


    /**
     * 时间表达式
     */
    private String cronStr;


    /**
     * 参数字符串，json
     */
    private JSONObject params;

    /**
     * 执行时间
     */
    private Date execTime;

    /**
     * 电子邮箱列表，用于发提醒邮件
     */
    private List<String> emails = new ArrayList<>();

    /**
     * 手机号列表，用于发提醒短信
     */
    private List<String> mobiles = new ArrayList<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronStr() {
        return cronStr;
    }

    public void setCronStr(String cronStr) {
        this.cronStr = cronStr;
    }

    /**
     * 获取任务执行参数
     */
    public <T> T getParam(String key, Class<T> clazz, T defaultValue) {
        if (params == null) {
            return null;
        }
        T value = params.getObject(key, clazz);
        return value == null ? defaultValue : value;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
