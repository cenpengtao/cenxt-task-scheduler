package cn.cenxt.task.mapper;

import cn.cenxt.task.model.ExecHistory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 执行历史映射
 */
public class ExecHistoryMapper implements RowMapper<ExecHistory> {

    @Override
    public ExecHistory mapRow(ResultSet resultSet, int i) throws SQLException {
        ExecHistory execHistory = new ExecHistory();
        execHistory.setId(resultSet.getInt("id"));
        execHistory.setTaskId(resultSet.getInt("task_id"));
        execHistory.setExecId(resultSet.getString("exec_id"));
        execHistory.setExecIp(resultSet.getString("exec_ip"));
        execHistory.setExecTime(resultSet.getTimestamp("exec_time"));
        execHistory.setFinishTime(resultSet.getTimestamp("finish_time"));
        execHistory.setCost(resultSet.getDouble("cost"));
        execHistory.setExecResult(resultSet.getInt("exec_result"));
        execHistory.setErrorMessage(resultSet.getString("error_message"));
        return execHistory;
    }
}