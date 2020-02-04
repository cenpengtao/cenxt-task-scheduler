package cn.cenxt.task.service;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.enums.ExecResultEnum;
import cn.cenxt.task.enums.TaskStatusEnum;
import cn.cenxt.task.mapper.ExecHistoryMapper;
import cn.cenxt.task.mapper.TaskRowMapper;
import cn.cenxt.task.model.ExecHistory;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.utils.CronAnalysisUtil;
import cn.cenxt.task.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 任务服务
 */
public class CenxtTaskService {

    private static final Logger logger = LoggerFactory.getLogger(CenxtTaskService.class);

    private JdbcTemplate jdbcTemplate;

    public CenxtTaskService() {

    }

    public CenxtTaskService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 初始化数据表
     */
    public void initTable() {
        logger.info("begin init table");
        logger.info(Constants.SQL_CREATE_TASK_TABLE);
        if(!checkTableExist(Constants.TABLE_NAME_TASK)){
            //创建任务表
            jdbcTemplate.update(Constants.SQL_CREATE_TASK_TABLE);
        }

        logger.info(Constants.SQL_CREATE_EXEC_HISTORY_TABLE);
        if(!checkTableExist(Constants.TABLE_NAME_EXEC_HISTORY)){
            //创建记录表
            jdbcTemplate.update(Constants.SQL_CREATE_EXEC_HISTORY_TABLE);
        }
        logger.info("success init table");
    }

    public boolean checkTableExist(String tableName){
        try{
            jdbcTemplate.update("SHOW CREATE TABLE "+tableName);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 获取可以执行的任务列表
     *
     * @param size 每次获取的条数
     * @return 任务列表
     */
    public List<Task> getWaitExecTask(int size) {
        String ip = IpUtil.getLocalIp();
        Object[] params = {ip, ip, size};
        return jdbcTemplate.query(Constants.SQL_QUERY_WAITE_EXEC_TASK_LIST, params, new TaskRowMapper());
    }

    /**
     * 锁定任务，将其修改成运行状态
     * 将处于待运行状态且满足执行条件的任务更新成执行中
     *
     * @param task 任务
     */
    public boolean lockTask(Task task) {
        return jdbcTemplate.update(Constants.SQL_QUERY_LOCK_TASK, IpUtil.getLocalIp(), task.getId(), task.getExecTime()) > 0;
    }


    /**
     * 释放任务，将任务修改成待运行或者失败状态
     *
     * @param task           任务
     * @param taskStatusEnum 执行结果
     */
    public void releaseTask(Task task, TaskStatusEnum taskStatusEnum) {
        try {
            jdbcTemplate.update(Constants.SQL_RELEASE_TASK, taskStatusEnum.getStatus(), CronAnalysisUtil.getNextTime(task.getCronStr(), task.getExecTime()), task.getId());
        } catch (Exception e) {
            logger.error("releaseTask error", e);
        }

    }


    /**
     * 失败并禁用
     *
     * @param id 任务编号
     */
    public void failAndDisableTask(int id) {
        try {
            jdbcTemplate.update(Constants.SQL_FAIL_AND_RELEASE_TASK, id);
        } catch (Exception e) {
            logger.error("failAndDisableTask error", e);
        }
    }


    /**
     * 新增执行记录
     *
     * @param task       任务
     * @param finishTime 结束时间
     * @param execResult 执行结果
     * @param message    执行信息
     */
    public void saveExecHistory(Task task, Date finishTime, ExecResultEnum execResult, String message) {
        try {
            long cost = finishTime == null ? 0 : (finishTime.getTime() - task.getExecTime().getTime());
            jdbcTemplate.update(Constants.SQL_INSERT_EXEC_HISTORY,
                    task.getId(), task.getExecId(), IpUtil.getLocalIp(), task.getExecTime(), finishTime,
                    cost, execResult.getResult(), 0, 0, 0, message);
        } catch (Exception e) {
            logger.error("insertExecHistory error", e);
        }

    }

    public void saveExecHistory(Task task, Date finishTime, ExecHistory execHistory) {
        if (execHistory.getExecMessage().length() > 10000) {
            execHistory.setExecMessage(execHistory.getExecMessage().substring(0, 10000));
        }
        try {
            jdbcTemplate.update(Constants.SQL_INSERT_EXEC_HISTORY,
                    task.getId(), task.getExecId(), IpUtil.getLocalIp(), task.getExecTime(), finishTime,
                    execHistory.getCost(), execHistory.getExecResult(), execHistory.getRetryTimes(),
                    execHistory.getExecReport().getSuccessCount(), execHistory.getExecReport().getFailCount(),
                    execHistory.getExecMessage());
        } catch (Exception e) {
            logger.error("insertExecHistory error", e);
        }
    }


    /**
     * 获取数据库当前时间
     *
     * @return 当前时间
     */
    public Date getNowTime() {
        try {
            return jdbcTemplate.queryForObject("SELECT NOW()", new RowMapper<Date>() {
                @Override
                public Date mapRow(ResultSet resultSet, int i) throws SQLException {
                    return resultSet.getTimestamp(1);
                }
            });
        } catch (Exception e) {
            logger.error("getNowTime error", e);
            return new Date();
        }
    }

    public List<Task> getAllTasks() {
        return jdbcTemplate.query(Constants.SQL_QUERY_ALL_TASK_LIST, new TaskRowMapper());
    }

    /**
     * 获取执行记录
     *
     * @param taskId 任务编号
     * @param size   条数
     * @return
     */
    public List<ExecHistory> getExecHistory(int taskId, int size) {
        Object[] params = {taskId, size};
        return jdbcTemplate.query(Constants.SQL_QUERY_EXEC_HISTORY_LIST, params, new ExecHistoryMapper());
    }

    /**
     * 获取错误的执行记录
     *
     * @param taskId 任务编号
     * @param size   条数
     * @return
     */
    public List<ExecHistory> getErrorExecHistory(int taskId, int size) {
        Object[] params = {taskId, size};
        return jdbcTemplate.query(Constants.SQL_QUERY_ERROR_EXEC_HISTORY_LIST, params, new ExecHistoryMapper());
    }

    /**
     * 删除执行记录
     *
     * @param date 什么时间之前
     * @param size 删除行数
     * @return 删除行数
     */
    public int deleteExecHistory(Date date, int size) {
        return jdbcTemplate.update(Constants.SQL_DELETE_EXEC_HISTORY, date, size);
    }

    /**
     * 保存任务
     *
     * @param task     任务
     * @param username 用户名
     */
    public void saveTask(Task task, String username) {
        if (task.getId() > 0) {
            jdbcTemplate.update(Constants.SQL_UPDATE_TASK,
                    task.getName(), task.getDescription(),
                    task.getCronStr(), task.getExpire(), task.getRetryTimes(),
                    task.getParams(), task.getNextTime(),
                    username,
                    task.getId());
        } else {
            jdbcTemplate.update(Constants.SQL_INSERT_TASK,
                    task.getName(), task.getDescription(),
                    task.getCronStr(), task.getExpire(), task.getRetryTimes(),
                    task.getParams(), task.getNextTime(),
                    username);
        }
    }

    /**
     * 修改任务启用状态
     *
     * @param id      任务编号
     * @param enabled 启用状态
     */
    public void enableTask(int id, boolean enabled, String username) {
        jdbcTemplate.update(Constants.SQL_UPDATE_TASK_ENABLE, enabled ? 1 : 0, username, id);
    }

    /**
     * 删除任务
     *
     * @param id 任务编号
     */
    public void deleteTask(int id) {
        jdbcTemplate.update(Constants.SQL_DELETE_TASK, id);
    }


}
