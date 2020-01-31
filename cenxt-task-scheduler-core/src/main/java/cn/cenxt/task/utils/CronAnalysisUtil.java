package cn.cenxt.task.utils;

import org.springframework.scheduling.support.CronSequenceGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CronAnalysisUtil {

    /**
     * @param cron cron表达式
     * @return 下次执行时间
     */
    public static Date getNextTime(String cron, Date now) {
        if (CronSequenceGenerator.isValidExpression(cron)) {
            return new CronSequenceGenerator(cron).next(now);
        } else {
            return null;
        }
    }

    /**
     * 获取n条下次执行的时间
     *
     * @param cron cron表达式
     * @param now  参考时间
     * @param size 条数
     * @return 时间列表
     */
    public static List<Date> getNextExecTimeList(String cron, Date now, int size) {
        List<Date> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            now = getNextTime(cron, now);
            if (now==null) {
                return list;
            }
            list.add(now);
        }
        return list;
    }
}
