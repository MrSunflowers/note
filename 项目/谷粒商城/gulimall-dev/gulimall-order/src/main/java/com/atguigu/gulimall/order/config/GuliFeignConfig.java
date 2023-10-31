package com.atguigu.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign配置类
 **/
@Configuration
public class GuliFeignConfig {

    /**
     * 注入拦截器
     * feign调用时根据拦截器构造请求头，封装cookie解决远程调用时无法获取springsession
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {
        // 创建拦截器
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                System.out.println("feign远程调用，拦截器封装请求头...RequestInterceptor.apply");
                // 1、使用RequestContextHolder拿到原生请求的请求头信（下文环境保持器）
                // 从ThreadLocal中获取请求头（要保证feign调用与controller请求处在同一线程环境）
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();// 获取controller请求对象
                    if (request != null) {
                        //2、同步请求头的数据（cookie）
                        String cookie = request.getHeader("Cookie");// 获取Cookie
                        template.header("Cookie", cookie);// 同步Cookie
                    }
                }
            }
        };
    }
}
