package cn.cenxt.task.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务
 */
public class Task implements Serializable {
    /**
     * 任务编号
     */
    private int id;
    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务描述
     */
    private String description;
    /**
     * 启用状态
     */
    private boolean enabled;
    /**
     * 执行状态：0待执行 1执行中 2执行失败
     */
    private int flag;

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
    private String params;

    /**
     * 执行时间
     */
    private Date execTime;
    /**
     * 执行ip
     */
    private String execIp;
    /**
     * 下次执行时间
     */
    private Date nextTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 更新人
     */
    private String updator;

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
        JSONObject jsonObject = null;
        if (!StringUtils.isEmpty(params)) {
            Object object = JSON.parse(params);
            if (object instanceof JSONObject) {
                jsonObject = (JSONObject) object;
            }
        }
        if (jsonObject == null) {
            return defaultValue;
        }
        T value = jsonObject.getObject(key, clazz);
        return value == null ? defaultValue : value;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getExecTime() {
        return execTime;
    }

    public void setExecTime(Date execTime) {
        this.execTime = execTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getParams() {
        return params;
    }

    public String getExecIp() {
        return execIp;
    }

    public void setExecIp(String execIp) {
        this.execIp = execIp;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public void setNextTime(Date nextTime) {
        this.nextTime = nextTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
