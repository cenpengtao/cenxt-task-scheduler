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
    private boolean enabled;

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

    /**
     * 任务表的前缀
     */
    private String tableNamePrefix = "";

    /**
     * 页面配置
     */
    private CenxtTaskProperties.View view = new CenxtTaskProperties.View();

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

    public String getTableNamePrefix() {
        return tableNamePrefix;
    }

    public void setTableNamePrefix(String tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    public void setInitTable(boolean initTable) {
        this.initTable = initTable;
    }

    public CenxtTaskProperties.View getView() {
        return view;
    }

    public void setView(CenxtTaskProperties.View view) {
        this.view = view;
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
         * 管理员用户名
         */
        private String adminUsername = "admin";
        /**
         * 管理员密码
         */
        private String adminPassword = "admin";
        /**
         * 游客用户名
         */
        private String normalUsername = "normal";
        /**
         * 游客密码
         */
        private String normalPassword = "normal";
        /**
         * 游客用户名
         */
        private String guestUsername = "guest";
        /**
         * 游客密码
         */
        private String guestPassword = "guest";

        /**
         * 单个IP最大登录尝试次数
         */
        private int maxTryCount = 5;


        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getAdminUsername() {
            return adminUsername;
        }

        public void setAdminUsername(String adminUsername) {
            this.adminUsername = adminUsername;
        }

        public String getAdminPassword() {
            return adminPassword;
        }

        public void setAdminPassword(String adminPassword) {
            this.adminPassword = adminPassword;
        }

        public String getNormalUsername() {
            return normalUsername;
        }

        public void setNormalUsername(String normalUsername) {
            this.normalUsername = normalUsername;
        }

        public String getNormalPassword() {
            return normalPassword;
        }

        public void setNormalPassword(String normalPassword) {
            this.normalPassword = normalPassword;
        }

        public String getGuestUsername() {
            return guestUsername;
        }

        public void setGuestUsername(String guestUsername) {
            this.guestUsername = guestUsername;
        }

        public String getGuestPassword() {
            return guestPassword;
        }

        public void setGuestPassword(String guestPassword) {
            this.guestPassword = guestPassword;
        }

        public int getMaxTryCount() {
            return maxTryCount;
        }

        public void setMaxTryCount(int maxTryCount) {
            this.maxTryCount = maxTryCount;
        }

    }
}
