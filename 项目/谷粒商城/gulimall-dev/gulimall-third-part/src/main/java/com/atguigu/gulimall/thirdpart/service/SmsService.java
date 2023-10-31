package com.atguigu.gulimall.thirdpart.service;

/**
 * 短信服务
 * @Author: wanzenghui
 * @Date: 2021/11/27 22:58
 */
public interface SmsService {

    /**
     * 发送短信验证码
     * @param phone 电话号码
     * @param code  验证码
     */
    public Boolean sendCode(String phone, String code);

}