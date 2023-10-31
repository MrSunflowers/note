package com.atguigu.gulimall.thirdpart.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdpart.service.impl.SmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信服务
 * @Author: wanzenghui
 * @Date: 2021/11/27 23:20
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    SmsServiceImpl smsService;

    /**
     * 发送短信验证码
     * 提供其他模块调用
     * @param phone 号码
     * @param code  验证码
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsService.sendCode(phone, code);
        return R.ok();
    }
}