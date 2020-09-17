package cn.cenxt.task.constants;

import cn.cenxt.task.enums.ExecResultEnum;
import cn.cenxt.task.enums.TaskStatusEnum;

/**
 * 常量
 */
public class Constants {
    /**
     * 最小任务扫描间隔
     */
    public static final int MIN_INTERVAL = 500;
    /**
     * 默认扫描间隔
     */
    public static final int DEFAULT_INTERVAL = 3000;

    /**
     * 默认获取任务条数
     */
    public static final int DEFAULT_FETCH_SIZE = 3;

    /**
     * 默认执行线程数
     */
    public static final int DEFAULT_THREAD_SIZE = 3;
    /**
     * 会话中用户名
     */
    public static final String SESSION_USERNAME = "CENXT_TASK_USERNAME";
    /**
     * 会话中角色
     */
    public static final String SESSION_ROLE = "CENXT_TASK_USER_ROLE";
    /**
     * 任务表名称
     */
    public static final String TABLE_NAME_TASK = "cenxt_task";
    /**
     * 执行记录表名词
     */
    public static final String TABLE_NAME_EXEC_HISTORY = "cenxt_exec_history";

    /**
     * 创建任务表Sql
     */
    public static final String SQL_CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS `"+TABLE_NAME_TASK+"` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
            "  `name` varchar(100) NOT NULL COMMENT '任务名称',\n" +
            "  `description` varchar(256) NOT NULL COMMENT '描述',\n" +
            "  `enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '启用状态',\n" +
            "  `flag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '执行状态：0待执行 1执行中 2执行失败',\n" +
            "  `cron_str` varchar(256) NOT NULL COMMENT '时间表达式',\n" +
            "  `expire` int(11) NOT NULL COMMENT '超时时间，单位分钟，0为不超时',\n" +
            "  `retry_times` int(11) NOT NULL COMMENT '重试次数',\n" +
            "  `params` varchar(4096) NOT NULL DEFAULT '' COMMENT '参数',\n" +
            "  `exec_time` timestamp NULL DEFAULT NULL COMMENT '执行时间',\n" +
            "  `exec_ip` varchar(50) DEFAULT NULL COMMENT '执行机器IP',\n" +
            "  `next_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下次执行时间',\n" +
            "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
            "  `creator` varchar(50) NOT NULL COMMENT '创建人',\n" +
            "  `updator` varchar(50) DEFAULT NULL COMMENT '更新人',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `name` (`name`),\n" +
            "  KEY `create_time` (`create_time`),\n" +
            "  KEY `update_time` (`update_time`),\n" +
            "  KEY `exec_time` (`exec_time`),\n" +
            "  KEY `next_time` (`next_time`),\n" +
            "  KEY `expire` (`expire`),\n" +
            "  KEY `exec_ip` (`exec_ip`)\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='任务表';";

    /**
     * 创建执行记录表Sql
     */
    public static final String SQL_CREATE_EXEC_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `"+TABLE_NAME_EXEC_HISTORY+"` (\n" +
            "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
            "  `task_id` bigint(20) NOT NULL COMMENT '任务编号',\n" +
            "  `exec_id` varchar(50) NOT NULL DEFAULT '' COMMENT '执行编号',\n" +
            "  `exec_ip` varchar(50) NOT NULL DEFAULT '' COMMENT '执行机器IP',\n" +
            "  `exec_time` timestamp NOT NULL COMMENT '执行时间',\n" +
            "  `finish_time` timestamp NULL DEFAULT NULL COMMENT '结束时间',\n" +
            "  `cost` double NOT NULL DEFAULT '0' COMMENT '执行耗时，单位ms',\n" +
            "  `exec_result` tinyint(2) NOT NULL COMMENT '执行结果：0待执行 1执行中 2执行成功 3重试中 4重试成功 5执行失败 6超时中断 ',\n" +
            "  `retry_times` int(11) DEFAULT '0' COMMENT '重试次数',\n" +
            "  `success_count` bigint(20) DEFAULT '0' COMMENT '成功记录数',\n" +
            "  `fail_count` bigint(20) DEFAULT '0' COMMENT '失败记录数',\n" +
            "  `exec_message` varchar(10240) DEFAULT '' COMMENT '执行信息',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `exec_id` (`exec_id`),\n" +
            "  KEY `exec_time` (`exec_time`),\n" +
            "  KEY `finish_time` (`finish_time`),\n" +
            "  KEY `task_id` (`task_id`),\n" +
            "  KEY `cost` (`cost`),\n" +
            "  KEY `exec_ip` (`exec_ip`),\n" +
            "  KEY `exec_result` (`exec_result`)\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='执行记录表';";

    /**
     * 查询可执行任务列表
     */
    public static final String SQL_QUERY_WAITE_EXEC_TASK_LIST = "SELECT * FROM " + TABLE_NAME_TASK + " WHERE \n" +
            "##任务是启用状态\n" +
            "enabled=1 \n" +
            "AND (\n" +
            "\t##任务待执行且下次执行时间小于当前时间\n" +
            "\t(flag=" + TaskStatusEnum.WAITING.getStatus() + " AND next_time<NOW()) \n" +
            "\t##任务为执行中，且任务已超时执行\n" +
            "\tOR (flag=" + TaskStatusEnum.RUNNING.getStatus() + " AND exec_time<DATE_SUB(NOW(),interval `expire`+1 MINUTE))\n" +
            "\tOR (\n" +
            "\t\t##执行失败\n" +
            "\t\tflag=" + TaskStatusEnum.FAIL.getStatus() + " \n" +
            "\t\tAND (\n" +
            "\t\t\t##如果上次执行机器为当前机器，执行时间延后1分钟\n" +
            "\t\t\t(exec_ip=? AND next_time<DATE_SUB(NOW(),interval 1 MINUTE))\n" +
            "\t\t\t##如果上次执行机器不是当前，到时间自动执行\n" +
            "\t\t\tOR (exec_ip<>? AND next_time<NOW())\n" +
            "\t\t)\n" +
            "\t)\n" +
            ") LIMIT ?";

    /**
     * 锁定任务
     */
    public static final String SQL_QUERY_LOCK_TASK = "UPDATE " + TABLE_NAME_TASK + " SET flag=" + TaskStatusEnum.RUNNING.getStatus() + ",exec_time=NOW(),exec_ip=? WHERE id=? AND enabled=1 AND (exec_time=? OR exec_time IS NULL)";
    /**
     * 释放任务
     */
    public static final String SQL_RELEASE_TASK = "UPDATE " + TABLE_NAME_TASK + " SET flag=?,next_time=? WHERE id=? AND flag=" + TaskStatusEnum.RUNNING.getStatus();
    /**
     * 失败并禁用任务
     */
    public static final String SQL_FAIL_AND_RELEASE_TASK = "UPDATE " + TABLE_NAME_TASK + " SET enabled=0,flag=" + TaskStatusEnum.FAIL.getStatus() + ",update_time=now(),updator='system' WHERE id=? AND enabled=1 ";

    /**
     * 新增执行记录
     */
    public static final String SQL_INSERT_EXEC_HISTORY = "INSERT INTO " + TABLE_NAME_EXEC_HISTORY + " (`task_id`, `exec_id`, `exec_ip`, `exec_time`, `finish_time`, `cost`, `exec_result`, `retry_times`, `success_count`, `fail_count`, `exec_message`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 更新执行记录
     */
    public static final String SQL_UPDATE_EXEC_HISTORY = "UPDATE " + TABLE_NAME_EXEC_HISTORY + " SET finish_time=?, cost=?, exec_result=?, retry_times=?, success_count=?, fail_count=?, exec_message=? WHERE exec_id=?";

    /**
     * 新增任务
     */
    public static final String SQL_INSERT_TASK = "INSERT INTO `" + TABLE_NAME_TASK + "` (`name`, `description`, `cron_str`, `expire`, `retry_times`, `params`, `next_time`, `create_time`, `creator`) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, now(), ?);";
    /**
     * 修改任务
     */
    public static final String SQL_UPDATE_TASK = "UPDATE `" + TABLE_NAME_TASK + "` SET `name`=?, `description`=?, `cron_str`=?, `expire`=?, `retry_times`=?, `params`=?, `next_time`=?,`updator`=?, `update_time`=now() WHERE  `id`=?";
    /**
     * 修改任务启用状态
     */
    public static final String SQL_UPDATE_TASK_ENABLE = "UPDATE `" + TABLE_NAME_TASK + "` SET `enabled`=?,`updator`=?, `update_time`=now() WHERE  `id`=?";
    /**
     * 删除任务
     */
    public static final String SQL_DELETE_TASK = "DELETE FROM `" + TABLE_NAME_TASK + "` WHERE  `id`=?";
    /**
     * 获取所有任务
     */
    public static final String SQL_QUERY_ALL_TASK_LIST = "SELECT * FROM " + TABLE_NAME_TASK + " order by update_time desc";

    /**
     * 通过exec_id获取最近执行记录
     */
    public static final String SQL_QUERY_EXEC_HISTORY_BY_EXEC_ID = "SELECT * FROM " + TABLE_NAME_EXEC_HISTORY + " WHERE exec_id = ?";

    /**
     * 获取最近n条执行记录
     */
    public static final String SQL_QUERY_EXEC_HISTORY_LIST = "SELECT * FROM " + TABLE_NAME_EXEC_HISTORY + " WHERE task_id=? order by exec_time DESC LIMIT ?";

    public static final String SQL_QUERY_ERROR_EXEC_HISTORY_LIST = "SELECT * FROM " + TABLE_NAME_EXEC_HISTORY + " WHERE task_id=? AND exec_result<>" + ExecResultEnum.SUCCESS.getResult() + " order by exec_time DESC LIMIT ?";
    /**
     * 删除执行记录
     */
    public static final String SQL_DELETE_EXEC_HISTORY = "DELETE FROM " + TABLE_NAME_EXEC_HISTORY + " WHERE exec_time<? LIMIT ?";
}
