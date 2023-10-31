package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userinfo")
    public String userInfo(@RequestParam("token") String token) {
        String username = redisTemplate.opsForValue().get(token);
        return username;
    }

    /**
     * 访问登录页
     * @param url       登录成功回调页
     * @param sso_token cookie值
     */
    @GetMapping(value = "/login.html")
    public String login(@RequestParam("redirect_url") String url, Model model,
                        @CookieValue(value = "sso_token", required = false) String token) {
        // 根据token获取用户信息
        if (!StringUtils.isEmpty(token)) {
            String username = redisTemplate.opsForValue().get(token);
            if (!StringUtils.isEmpty(username)) {
                // token正确，已登录状态，跳转回客户端【当前访问客户端共享了其他客户端的登录状态】
                return "redirect:" + url + "?token=" + token;
            }
        }
        // 不存在sso_token，未登录返回登录页，并将回调地址链路下传
        model.addAttribute("url", url);
        return "login";
    }

    /**
     * 登录
     * @param url       登录成功回调页
     */
    @PostMapping(value = "/doLogin")
    public String doLogin(String username, String password, String url,
                          Model model, HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            // 登录成功，跳转回调页
            String token = UUID.randomUUID().toString().replace("-", "");
            // token作为key，用户信息作为value存入redis中
            redisTemplate.opsForValue().set(token, username);
            // 在sso.com域名下设置cookie，使得不同客户端访问单点登录时可以带上cookie值成功登录
            Cookie cookie = new Cookie("sso_token", token);
            response.addCookie(cookie);
            return "redirect:" + url + "?token=" + token;
        }
        // 登录失败
        model.addAttribute("url", url);
        return "login";
    }

}
