package cn.cenxt.task.mapper;

import cn.cenxt.task.model.Task;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 任务映射
 */
public class TaskRowMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet resultSet, int i) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getInt("id"));
        task.setName(resultSet.getString("name"));
        task.setEnabled(resultSet.getBoolean("enabled"));
        task.setFlag(resultSet.getInt("flag"));
        task.setName(resultSet.getString("name"));
        task.setDescription(resultSet.getString("description"));
        task.setExpire(resultSet.getInt("expire"));
        task.setExecTime(resultSet.getTimestamp("exec_time"));
        task.setExecIp(resultSet.getString("exec_ip"));
        task.setRetryTimes(resultSet.getInt("retry_times"));
        task.setCronStr(resultSet.getString("cron_str"));
        task.setNextTime(resultSet.getTimestamp("next_time"));
        task.setCreateTime(resultSet.getTimestamp("create_time"));
        task.setUpdateTime(resultSet.getTimestamp("update_time"));
        task.setCreator(resultSet.getString("creator"));
        task.setUpdator(resultSet.getString("updator"));
        task.setParams(resultSet.getString("params"));
        return task;
    }
}
