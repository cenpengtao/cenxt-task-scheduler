//package cn.cenxt.taskdemo;
//
//import cn.cenxt.task.enums.RoleEnum;
//import cn.cenxt.task.listeners.CenxtTaskListener;
//import cn.cenxt.task.model.Task;
//import cn.cenxt.task.service.CenxtSecurityService;
//import org.springframework.stereotype.Service;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 测试重写CenxtSecurityService、CenxtTaskListener
// */
//@Service
//public class TaskService implements CenxtSecurityService, CenxtTaskListener {
//    /**
//     * 获取用户名
//     *
//     * @param request 请求
//     * @return 角色 如果为null，表示未登录
//     */
//    @Override
//    public String getUserName(HttpServletRequest request) {
//        return "123";
//    }
//
//    /**
//     * 获取角色
//     *
//     * @param request 请求
//     * @return 角色 如果为null，表示未登录
//     */
//    @Override
//    public RoleEnum getRole(HttpServletRequest request) {
//        return RoleEnum.ADMIN;
//    }
//
//    /**
//     * 任务开始
//     *
//     * @param task 任务
//     */
//    @Override
//    public void begin(Task task) {
//
//    }
//
//    /**
//     * 任务执行成果并结束
//     *
//     * @param task       任务
//     * @param cost       耗时，单位ms
//     * @param retryTimes 重试次数
//     */
//    @Override
//    public void finish(Task task, long cost, int retryTimes) {
//
//    }
//
//    /**
//     * 任务执行失败并结束
//     *
//     * @param task       任务
//     * @param cost       耗时，单位ms
//     * @param retryTimes 重试次数
//     */
//    @Override
//    public void exceptionFinish(Task task, long cost, int retryTimes) {
//
//    }
//
//    /**
//     * 任务执行失败
//     *
//     * @param task  任务
//     * @param cost  单次执行耗时，单位ms
//     * @param times 执行次数，从1开始
//     * @param e     异常信息
//     */
//    @Override
//    public void fail(Task task, long cost, int times, Exception e) {
//
//    }
//
//    /**
//     * 任务重试
//     *
//     * @param task       任务
//     * @param retryTimes 重试次数，从1开始
//     */
//    @Override
//    public void retry(Task task, int retryTimes) {
//
//    }
//}
