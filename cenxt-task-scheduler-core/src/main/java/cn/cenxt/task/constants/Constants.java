package cn.cenxt.task.constants;

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
     * 创建任务表Sql
     */
    public static final String SQL_CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS `cenxt_task` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
            "  `name` varchar(100) NOT NULL COMMENT '任务名称',\n" +
            "  `description` varchar(256) NOT NULL COMMENT '描述',\n" +
            "  `enabled` tinyint(1) NOT NULL COMMENT '启用状态',\n" +
            "  `flag` tinyint(2) NOT NULL COMMENT '执行状态：0待执行 1执行中 2执行失败',\n" +
            "  `cron_str` varchar(256) NOT NULL COMMENT '时间表达式',\n" +
            "  `expire` int(11) NOT NULL COMMENT '超时时间，单位分钟，0为不超时',\n" +
            "  `retry_times` int(11) NOT NULL COMMENT '重试次数',\n" +
            "  `params` varchar(4096) NOT NULL DEFAULT '' COMMENT '参数',\n" +
            "  `exec_time` timestamp NOT NULL COMMENT '执行时间',\n" +
            "  `exec_ip` varchar(50) NOT NULL DEFAULT '' COMMENT '执行机器IP',\n" +
            "  `next_time` timestamp NOT NULL COMMENT '下次执行时间',\n" +
            "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
            "  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',\n" +
            "  `emails` varchar(4096) NOT NULL COMMENT '邮箱列表',\n" +
            "  `mobiles` varchar(4096) NOT NULL COMMENT '手机号列表',\n" +
            "  `creator` varchar(50) NOT NULL COMMENT '创建人',\n" +
            "  `updator` varchar(50) NOT NULL DEFAULT '' COMMENT '更新人',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `name` (`name`),\n" +
            "  KEY `create_time` (`create_time`),\n" +
            "  KEY `update_time` (`update_time`),\n" +
            "  KEY `exec_time` (`exec_time`),\n" +
            "  KEY `next_time` (`next_time`),\n" +
            "  KEY `expire` (`expire`),\n" +
            "  KEY `exec_ip` (`exec_ip`)\n" +
            ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='任务表';";

    /**
     * 创建执行记录表Sql
     */
    public static final String SQL_CREATE_EXEC_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `cenxt_exec_history` (\n" +
            "  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
            "  `task_id` bigint(20) NOT NULL COMMENT '任务编号',\n" +
            "  `exec_id` varchar(50) NOT NULL DEFAULT '' COMMENT '执行编号',\n" +
            "  `exec_ip` varchar(50) NOT NULL DEFAULT '' COMMENT '执行机器IP',\n" +
            "  `exec_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',\n" +
            "  `finish_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',\n" +
            "  `cost` double NOT NULL DEFAULT '0' COMMENT '执行耗时，单位ms',\n" +
            "  `exec_result` tinyint(2) NOT NULL COMMENT '执行结果：0成功 1重试后成功 2失败',\n" +
            "  `error_message` varchar(10240) NOT NULL DEFAULT '' COMMENT '错误信息',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `exec_time` (`exec_time`),\n" +
            "  KEY `finish_time` (`finish_time`),\n" +
            "  KEY `task_id` (`task_id`),\n" +
            "  KEY `cost` (`cost`),\n" +
            "  KEY `exec_id` (`exec_id`),\n" +
            "  KEY `exec_ip` (`exec_ip`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行记录表';";

    /**
     * 查询可执行任务列表
     */
    public static final String SQL_QUERY_WAITE_EXEC_TASK_LIST = "SELECT * FROM cenxt_task WHERE \n" +
            "##任务是启用状态\n" +
            "enabled=1 \n" +
            "AND (\n" +
            "\t##任务待执行且下次执行时间小于当前时间\n" +
            "\t(flag=0 AND next_time<NOW()) \n" +
            "\t##任务为执行中，且任务已超时执行\n" +
            "\tOR (flag=1 AND exec_time<DATE_SUB(NOW(),interval `expire` MINUTE))\n" +
            "\tOR (\n" +
            "\t\t##执行失败\n" +
            "\t\tflag=2 \n" +
            "\t\tAND (\n" +
            "\t\t\t##如果上次执行机器为当前机器，执行时间延后1分钟\n" +
            "\t\t\t(exec_ip=? AND next_time<DATE_ADD(NOW(),interval 1 MINUTE))\n" +
            "\t\t\t##如果上次执行机器不是当前，到时间自动执行\n" +
            "\t\t\tOR (exec_ip<>? AND next_time<NOW())\n" +
            "\t\t)\n" +
            "\t)\n" +
            ") LIMIT ?";

    /**
     * 锁定任务
     */
    public static final String SQL_QUERY_LOCK_TASK = "UPDATE cenxt_task SET flag=1,exec_time=NOW(),exec_ip=? WHERE id=? AND enabled=1 AND exec_time=?";
    /**
     * 释放任务
     */
    public static final String SQL_RELEASE_TASK = "UPDATE cenxt_task SET flag=?,next_time=? WHERE id=? AND flag=1 ";
    /**
     * 失败并禁用任务
     */
    public static final String SQL_QUERY_RELEASE_TASK = "UPDATE cenxt_task SET enabled=0,flag=2,update_time=now(),updator='system' WHERE id=? AND enabled=1 ";

    /**
     * 新增执行记录
     */
    public static final String SQL_INSERT_EXEC_HISTORY = "INSERT INTO cenxt_exec_history (`task_id`, `exec_id`, `exec_ip`, `exec_time`, `finish_time`, `cost`, `exec_result`, `error_message`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
}
