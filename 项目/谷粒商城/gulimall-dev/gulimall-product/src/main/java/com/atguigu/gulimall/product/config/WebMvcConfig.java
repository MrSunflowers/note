package com.atguigu.gulimall.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加处理器
     * 将所有/static访问静态资源的请求映射到/static类路径下
     *
     * 使用该处理器的场景：
     *      templates拷贝的是自己之前改动过的gulimall项目，动静分离时静态资源访问路径前都添加了/static/导致了无法在现有项目的
     *      类路径下访问到静态资源，有3种解决方案
     *          1.创建以下目录结构 resources/static/static/index
     *          2.创建以下目录结构 resources/static/index，删除引用静态资源时的/static前缀
     *          3.创建以下目录结构 resources/static/index，增加以下处理器重定义加载静态资源的类路径classpath:/static/
     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

}