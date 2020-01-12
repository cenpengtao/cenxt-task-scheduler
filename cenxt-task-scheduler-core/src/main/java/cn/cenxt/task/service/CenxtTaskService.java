package cn.cenxt.task.service;

import cn.cenxt.task.Task;
import cn.cenxt.task.constants.Constants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 任务服务
 */
public class CenxtTaskService {

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
        jdbcTemplate.update(Constants.SQL_CREATE_TASK_TABLE);
        jdbcTemplate.update(Constants.SQL_CREATE_EXEC_HISTORY_TABLE);
    }

    /**
     * 获取可以执行的任务列表
     *
     * @param size 每次获取的条数
     * @return 任务列表
     */
    public List<Task> getWaitExecTask(int size) {
        return jdbcTemplate.query(Constants.SQL_QUERY_WAITE_EXEC_TASK_LIST, new RowMapper<Task>() {
            @Override
            public Task mapRow(ResultSet resultSet, int i) throws SQLException {
                Task task = new Task();
                //TODO
                return task;
            }
        });
    }

    /**
     * 锁定任务，将其修改成运行状态
     * 将处于待运行状态且满足执行条件的任务更新成执行中
     *
     * @param id
     */
    public boolean lockTask(int id) {
        return jdbcTemplate.update(Constants.SQL_QUERY_LOCK_TASK, id) > 0;
    }
}
