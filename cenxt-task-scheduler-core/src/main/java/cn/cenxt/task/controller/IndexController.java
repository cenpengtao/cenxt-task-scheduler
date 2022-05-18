package cn.cenxt.task.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能描述：首页控制器
 */
@RequestMapping("/cenxt-task-view")
@Controller
public class IndexController {

    @RequestMapping("/")
    public ResponseEntity<String> index(HttpServletResponse response) throws IOException {

        response.sendRedirect("./index.html");
        return null;
    }
}
