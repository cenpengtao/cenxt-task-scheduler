package cn.cenxt.task.controller;

import cn.cenxt.task.constants.Constants;
import cn.cenxt.task.enums.RoleEnum;
import cn.cenxt.task.jobs.CenxtJob;
import cn.cenxt.task.model.CronExplain;
import cn.cenxt.task.model.ExecHistory;
import cn.cenxt.task.model.Login;
import cn.cenxt.task.model.Task;
import cn.cenxt.task.properties.CenxtTaskProperties;
import cn.cenxt.task.service.CenxtSecurityService;
import cn.cenxt.task.service.CenxtTaskService;
import cn.cenxt.task.utils.CronAnalysisUtil;
import cn.cenxt.task.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述：接口控制器
 */
@RestController
@RequestMapping("/cenxt-task-view/api")
public class ApiController {

    @Autowired
    private CenxtTaskService cenxtTaskService;
    @Autowired
    private CenxtSecurityService cenxtSecurityService;
    @Autowired
    private CenxtTaskProperties properties;

    @Autowired
    private ApplicationContext applicationContext;

    private static Map<String, Integer> loginMap = new ConcurrentHashMap<>();

    /**
     * 登录
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Login login, HttpServletRequest request) {
        if (StringUtils.isEmpty(login.getPassword()) || StringUtils.isEmpty(login.getUsername())) {
            return new ResponseEntity<>("用户名或密码不能为空", HttpStatus.BAD_REQUEST);
        }
        String ip = IpUtil.getIpAddr(request);
        if (loginMap.containsKey(ip) && loginMap.get(ip) >= properties.getView().getMaxTryCount()) {
            return new ResponseEntity<>("登录失败超过限制次数", HttpStatus.BAD_REQUEST);
        }
        RoleEnum role = null;
        if (login.getUsername().equals(properties.getView().getAdminUsername()) && login.getPassword().equals(properties.getView().getAdminPassword())) {
            role = RoleEnum.ADMIN;
        } else if (login.getUsername().equals(properties.getView().getGuestUsername()) && login.getPassword().equals(properties.getView().getGuestPassword())) {
            role = RoleEnum.GUEST;
        }
        if (role == null) {
            if (loginMap.containsKey(ip)) {
                loginMap.put(ip, loginMap.get(ip) + 1);
            } else {
                loginMap.put(ip, 1);
            }
            return new ResponseEntity<>("用户名或密码错误", HttpStatus.BAD_REQUEST);
        }
        request.getSession().setAttribute(Constants.SESSION_USERNAME, login.getUsername());
        request.getSession().setAttribute(Constants.SESSION_ROLE, role);
        //登录成功后删除
        loginMap.remove(ip);
        return ResponseEntity.ok(role.toString());
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.removeAttribute(Constants.SESSION_USERNAME);
        session.removeAttribute(Constants.SESSION_ROLE);
        return ResponseEntity.ok("");
    }

    /**
     * 获取所有任务列表
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> tasks() {
        return ResponseEntity.ok(cenxtTaskService.getAllTasks());
    }

    /**
     * 获取所有任务列表
     */
    @GetMapping("/jobs")
    public ResponseEntity<String[]> jobs() {
        return ResponseEntity.ok(applicationContext.getBeanNamesForType(CenxtJob.class));
    }

    /**
     * 获取cron执行计划
     */
    @PostMapping("/cron-explain")
    public ResponseEntity<List<Date>> cronExpalin(@RequestBody CronExplain cronExplain) {
        return ResponseEntity.ok(CronAnalysisUtil.getNextExecTimeList(cronExplain.getCronStr(), new Date(), cronExplain.getSize()));
    }

    @GetMapping("/exec-history/{taskId}/{size}")
    public ResponseEntity<List<ExecHistory>> execHistory(@PathVariable("taskId") int taskId,
                                                         @PathVariable("size") int size) {
        if(size<1||size>100){
            size=10;
        }
        return ResponseEntity.ok(cenxtTaskService.getExecHistory(taskId, size));
    }


    @PostMapping("/admin/task")
    public ResponseEntity<String> saveTask(@RequestBody Task task,
                                           @SessionAttribute(name = Constants.SESSION_USERNAME) String username){
        cenxtTaskService.saveTask(task,username);
        return ResponseEntity.ok("");
    }
    @PostMapping("/admin/task/enabled/{id}")
    public ResponseEntity<String> enabledTask(@PathVariable("id") int id,
                                           @SessionAttribute(name = Constants.SESSION_USERNAME) String username){
        cenxtTaskService.enableTask(id,true,username);
        return ResponseEntity.ok("");
    }
    @PostMapping("/admin/task/disabled/{id}")
    public ResponseEntity<String> disabledTask(@PathVariable("id") int id,
                                               @SessionAttribute(name = Constants.SESSION_USERNAME) String username){
        cenxtTaskService.enableTask(id,false,username);
        return ResponseEntity.ok("");
    }
    @DeleteMapping("/admin/task/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable("id") int id){
        cenxtTaskService.deleteTask(id);
        return ResponseEntity.ok("");
    }
}
