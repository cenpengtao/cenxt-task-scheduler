package cn.cenxt.task.properties;

import cn.cenxt.task.constants.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 任务配置项
 */
@ConfigurationProperties("cenxt.task")
public class CenxtTaskProperties {

    /**
     * 是否启用配置
     */
    private boolean enabled = true;

    /**
     * 任务扫描间隔单位ms
     */
    private int scanInterval = Constants.DEFAULT_INTERVAL;

    /**
     * 每次获取待执行任务条数
     */
    private int fetchSize = Constants.DEFAULT_FETCH_SIZE;

    /**
     * 任务执行线程数
     */
    private int thread = Constants.DEFAULT_THREAD_SIZE;

    /**
     * 初始化任务表，如果启用，不存在任务表将自动创建
     */
    private boolean initTable = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public boolean isInitTable() {
        return initTable;
    }

    public void setInitTable(boolean initTable) {
        this.initTable = initTable;
    }

    /**
     * 控制界面配置
     */
    public static class View {
        /**
         * 是否启用控制界面
         */
        private boolean enabled;
        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * 允许IP配置
         */
        private String allow;
        /**
         * 单个IP最大登录尝试次数
         */
        private int MaxTryCount;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAllow() {
            return allow;
        }

        public void setAllow(String allow) {
            this.allow = allow;
        }

        public int getMaxTryCount() {
            return MaxTryCount;
        }

        public void setMaxTryCount(int maxTryCount) {
            MaxTryCount = maxTryCount;
        }
    }
}
