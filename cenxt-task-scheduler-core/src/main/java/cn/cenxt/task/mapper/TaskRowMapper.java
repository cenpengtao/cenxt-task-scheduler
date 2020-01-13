package cn.cenxt.task.mapper;

import cn.cenxt.task.model.Task;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 任务映射
 */
public class TaskRowMapper implements RowMapper<Task> {

    @Override
    public Task mapRow(ResultSet resultSet, int i) throws SQLException {
        Task task = new Task();
        task.setId(resultSet.getInt("id"));
        task.setName(resultSet.getString("name"));
        task.setDescription(resultSet.getString("description"));
        task.setExpire(resultSet.getInt("expire"));
        task.setExecTime(resultSet.getDate("exec_time"));
        task.setRetryTimes(resultSet.getInt("retry_times"));
        task.setCronStr(resultSet.getString("cron_str"));

        String emailStr = resultSet.getString("emails");
        if (!StringUtils.isEmpty(emailStr)) {
            task.setEmails(Arrays.asList(emailStr.split(";")));
        }
        String mobilesStr = resultSet.getString("mobiles");
        if (!StringUtils.isEmpty(mobilesStr)) {
            task.setMobiles(Arrays.asList(mobilesStr.split(";")));
        }

        String paramStr = resultSet.getString("params");
        if (!StringUtils.isEmpty(paramStr)) {
            Object object = JSON.parse(paramStr);
            if (object instanceof JSONObject) {
                JSONObject params = (JSONObject) object;
                task.setParams(params);
            }
        }
        return task;
    }
}
