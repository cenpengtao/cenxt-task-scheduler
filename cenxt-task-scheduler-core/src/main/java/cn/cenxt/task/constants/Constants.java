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
     * 创建任务表Sql预计
     */
    public static final String SQL_CREATE_TASK_TABLE = "CREATE TABLE IF NOT EXISTS `cenxt_task` (\n" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',\n" +
            "  `name` varchar(100) NOT NULL COMMENT '任务名称',\n" +
            "  `description` varchar(256) NOT NULL COMMENT '描述',\n" +
            "  `enabled` tinyint(1) NOT NULL COMMENT '启用状态',\n" +
            "  `flag` tinyint(2) NOT NULL COMMENT '执行状态：0待执行 1执行中 2执行成功 3执行失败',\n" +
            "  `time_type` tinyint(2) NOT NULL COMMENT '时间控制类型',\n" +
            "  `time_value` varchar(256) NOT NULL COMMENT '时间控制值',\n" +
            "  `expire` int(11) NOT NULL COMMENT '超时时间，单位分钟，0为不超时',\n" +
            "  `params` varchar(4096) NOT NULL DEFAULT '' COMMENT '参数',\n" +
            "  `exec_time` timestamp NOT NULL COMMENT '执行时间',\n" +
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
            "  KEY `expire` (`expire`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';";

    /**
     * 创建执行记录表Sql预计
     */
    public static final String SQL_CREATE_EXEC_HISTORY_TABLE = "CREATE TABLE IF NOT EXISTS `cenxt_exec_history` (\n" +
            "  `id` bigint(20) NOT NULL COMMENT '编号',\n" +
            "  `task_id` bigint(20) NOT NULL COMMENT '任务编号',\n" +
            "  `exec_id` varchar(50) NOT NULL DEFAULT '' COMMENT '执行编号',\n" +
            "  `exec_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',\n" +
            "  `finish_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',\n" +
            "  `cost` double NOT NULL DEFAULT '0' COMMENT '执行耗时，单位ms',\n" +
            "  `exec_result` tinyint(2) NOT NULL COMMENT '执行结果：0成功 1重试后成功 2失败',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  KEY `exec_time` (`exec_time`),\n" +
            "  KEY `finish_time` (`finish_time`),\n" +
            "  KEY `task_id` (`task_id`),\n" +
            "  KEY `cost` (`cost`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行记录表';";

    /**
     * 查询可执行任务列表
     */
    public static final String SQL_QUERY_WAITE_EXEC_TASK_LIST = "";

    /**
     * 锁定任务
     */
    public static final String SQL_QUERY_LOCK_TASK = "UPDATE cenxt_task SET flag=1 WHERE id=? AND flag=0 AND next_time<NOW()";
}
