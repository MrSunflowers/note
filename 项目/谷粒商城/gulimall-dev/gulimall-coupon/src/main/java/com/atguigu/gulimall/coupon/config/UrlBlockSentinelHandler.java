package com.atguigu.gulimall.coupon.config;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: Url资源限流请求自定义返回
 **/
@Component
public class UrlBlockSentinelHandler implements BlockExceptionHandler {

    /**
     * 自定义限流返回信息
     *
     * @param request
     * @param response
     * @param ex
     * @throws IOException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMsg());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(error));
    }
}
