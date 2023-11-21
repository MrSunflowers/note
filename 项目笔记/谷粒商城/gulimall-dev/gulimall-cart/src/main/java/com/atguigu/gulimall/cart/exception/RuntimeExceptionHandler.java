package com.atguigu.gulimall.cart.exception;

import com.atguigu.common.utils.R;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 * @Author: wanzenghui
 * @Date: 2021/12/9 0:01
 */
@ControllerAdvice
public class RuntimeExceptionHandler {
    /**
     * 全局统一异常处理
     */
    @ExceptionHandler(CartExceptionHandler.class)
    public R userHandler(CartExceptionHandler exception) {
        return R.error("购物车无此商品");
    }

    /**
     * 全局统一异常处理
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public R handler(RuntimeException exception) {
        return R.error(exception.getMessage());
    }
}