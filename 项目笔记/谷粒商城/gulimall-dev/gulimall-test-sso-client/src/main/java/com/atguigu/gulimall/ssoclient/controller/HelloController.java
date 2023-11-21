package com.atguigu.gulimall.ssoclient.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wanzenghui
 * @Date: 2021/12/2 0:03
 */
@Controller
public class HelloController {

    /**
     * 需要登录状态访问
     */
    @GetMapping(value = "/employees")
    public String employees(Model model, HttpSession session,
                            @RequestParam(name = "token", required = false) String token) {
        if (!StringUtils.isEmpty(token)) {
            // 根据token去sso认证中心获取用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> entity = restTemplate.getForEntity("http://sso.com:8080/userinfo?token=" + token, String.class);
            session.setAttribute("loginUser", entity.getBody());
        }
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null && token == null) {
            // 未登录,跳转认证服务器登录
            return "redirect:http://sso.com:8080/login.html?redirect_url=http://client1.com:8081/employees";
        } else {
            // 登录状态显示
            List<String> emps = new ArrayList<>();
            emps.add("张三");
            emps.add("李四");
            model.addAttribute("emps", emps);
            return "employees";
        }

    }
}