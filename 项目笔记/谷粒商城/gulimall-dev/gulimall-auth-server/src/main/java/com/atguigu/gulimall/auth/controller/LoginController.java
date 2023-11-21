package com.atguigu.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.auth.AuthConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.auth.MemberResponseVO;
import com.atguigu.common.vo.auth.UserLoginVO;
import com.atguigu.common.vo.auth.UserRegisterVO;
import com.atguigu.gulimall.auth.feign.MemberFeignService;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 登录
 *
 * @Author: wanzenghui
 * @Date: 2021/11/26 22:26
 */
@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 访问登录页面
     * 登录状态自动跳转首页
     */
    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session) {
        // 判断是否登录状态
        Object attribute = session.getAttribute(AuthConstant.LOGIN_USER);
        if (attribute == null) {
            // 未登录，返回登录页资源
            return "login";
        } else {
            // 已登录
            return "redirect:http://gulimall.com";
        }
    }

//    /**
//     * 访问注册页面（该方法已省略，使用视图控制器简化代码）
//     */
//    @GetMapping(value = "/reg.html")
//    public String regPage(HttpSession session) {
//        return "reg";
//    }

    /**
     * 发送短信验证码
     *
     * @param phone 号码
     * @param code  验证码
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam(name = "phone", required = true) String phone) {
        // 1.判断60秒间隔发送，防刷
        String _code = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.isNotBlank(_code) && System.currentTimeMillis() - Long.parseLong(_code.split("_")[1]) < 60000) {
            // 调用接口小于60秒间隔不允许重新发送新的验证码
            return R.error(BizCodeEnume.SMS_CODE_EXCEPTION);
        }

        // 2.验证码存入缓存
        String code = UUID.randomUUID().toString().substring(0, 5);
        // 验证码缓存到redis中（并且记录当前时间戳）
        redisTemplate.opsForValue().set(AuthConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 10, TimeUnit.MINUTES);

        // 3.发送验证码
        thirdPartFeignService.sendCode(phone, code);

        return R.ok();
    }

    /**
     * 注册接口
     *
     * @param user       接收注册信息
     * @param result     接收参数校验结果
     * @param attributes 重定向保存数据（原理：使用session，重定向请求后根据cookie拿到session的数据）TODO 分布式session
     */
    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVO user, BindingResult result, RedirectAttributes attributes) {
        // 1.参数校验
        if (result.hasErrors()) {
            // 校验出错，返回注册页
            Map<String, String> errMap = new HashMap<>();
            result.getFieldErrors().forEach(err -> errMap.put(err.getField(), err.getDefaultMessage()));
            // 封装异常返回前端显示
            attributes.addFlashAttribute("errors", errMap);// flash，session中的数据只使用一次
            return "redirect:http://auth.gulimall.com/reg.html";// 采用重定向有一定防刷功能
            // 1、return "redirect:http://auth.gulimall.com/reg.html"【采用】 重定向Get请求【配合RedirectAttributes共享数据】
            // 2、return "redirect:http:/reg.html"                   【采用】 重定向Get请求，省略当前服务url【配合RedirectAttributes共享数据】
            // 3、return "redirect:/reg.html"                                重定向Get请求，使用视图控制器拦截请求并映射reg视图【配合RedirectAttributes共享数据】【bug：会以ip+port来重定向】
            // 4、return "forward:http://auth.gulimall.com/reg.html";        请求转发与当前请求方式一致（Post请求）【配合Model共享数据】【异常404：当前/reg.html不存在post请求】
            // 5、return "forward:http:/reg.html";                           请求转发与当前请求方式一致（Post请求），省略当前服务url 【配合Model共享数据】【异常404：当前/reg.html不存在post请求】
            // 6、return "forward:/reg.html";                                请求转发与当前请求方式一致（Post请求），使用视图控制器拦截请求并映射reg视图【配合Model共享数据】【异常405：Request method 'POST' not supported，视图控制器必须使用GET请求访问，而当前请求转发使用post方式，导致异常】
            // 7、return "reg";                                              视图解析器前后拼串查找资源返回【配合Model共享数据】
        }

        // 2.验证码校验
        String code = user.getCode();
        String redisCode = redisTemplate.opsForValue().get(AuthConstant.SMS_CODE_CACHE_PREFIX + user.getPhone());
        if (StringUtils.isBlank(redisCode)) {
            // 验证码过期
            Map<String, String> errMap = new HashMap<>();
            errMap.put("code", "验证码失效");
            // 封装异常返回前端显示
            attributes.addFlashAttribute("errors", errMap);// flash，session中的数据只使用一次
            return "redirect:http://auth.gulimall.com/reg.html";// 采用重定向有一定防刷功能
        }
        if (!code.equals(redisCode.split("_")[0])) {
            // 验证码错误
            Map<String, String> errMap = new HashMap<>();
            errMap.put("code", "验证码错误");
            // 封装异常返回前端显示
            attributes.addFlashAttribute("errors", errMap);// flash，session中的数据只使用一次
            return "redirect:http://auth.gulimall.com/reg.html";// 采用重定向有一定防刷功能
        }

        // 3.调用login实现注册
        redisTemplate.delete(AuthConstant.SMS_CODE_CACHE_PREFIX + user.getPhone());
        R r = memberFeignService.regist(user);
        if (r.getCode() == 0) {
            // 注册成功，重定向到登录页
            return "redirect:http://auth.gulimall.com/login.html";// 重定向
        } else {
            //　注册失败，封装异常
            HashMap<String, String> errMap = new HashMap<>();
            errMap.put("msg", r.getData("msg", new TypeReference<String>() {
            }));
            attributes.addFlashAttribute("errors", errMap);// flash，session中的数据只使用一次
            return "redirect:http://auth.gulimall.com/reg.html";// 采用重定向有一定防刷功能
        }
    }

    /**
     * 登录接口
     */
    @PostMapping(value = "/login")
    public String login(UserLoginVO user, RedirectAttributes attributes, HttpSession session) {
        // 1.远程调用登录
        R r = memberFeignService.login(user);
        if (r.getCode() == 0) {
            // 2.登录成功，设置session值
            MemberResponseVO data = r.getData(new TypeReference<MemberResponseVO>() {
            });
            session.setAttribute(AuthConstant.LOGIN_USER, data);
            // 3.重定向，视图可以从session中拿到用户信息
            return "redirect:http://gulimall.com";
        } else {
            // 4.登录失败，封装异常信息重定向返回
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>() {}));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}