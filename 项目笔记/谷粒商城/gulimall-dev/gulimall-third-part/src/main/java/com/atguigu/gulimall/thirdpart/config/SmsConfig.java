package com.atguigu.gulimall.thirdpart.config;

import com.atguigu.gulimall.thirdpart.service.SmsService;
import com.atguigu.gulimall.thirdpart.service.impl.SmsServiceImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信配置类
 * @Author: wanzenghui
 * @Date: 2021/11/27 23:01
 */
@Configuration
public class SmsConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
    public SmsServiceImpl smsService() {
        return new SmsServiceImpl();
    }

}