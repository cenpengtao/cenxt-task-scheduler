package cn.cenxt.task.annotations;

import cn.cenxt.task.enums.RoleEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 任务信息
 */
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TaskInfo {

    /**
     * 任务描述
     *
     * @return 任务描述
     */
    String description();

    /**
     * 参数描述
     * 问号开头表示字段描述
     *
     * @return 参数描述
     */
    String paramsDescription();

    /**
     * 时间表达式
     *
     * @return 时间表达式
     */
    String cron() default "0 0 2 */1 * ?";

    /**
     * 过期时间（分钟）
     *
     * @return 过期时间
     */
    int expire() default 10;

    /**
     * 重试次数
     *
     * @return 重试次数
     */
    int retryTimes() default 0;


    /**
     * @return
     */
    RoleEnum role() default RoleEnum.NORMAL;

}
