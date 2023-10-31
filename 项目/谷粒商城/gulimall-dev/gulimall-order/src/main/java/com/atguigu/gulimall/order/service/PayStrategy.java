package com.atguigu.gulimall.order.service;

import com.alipay.api.AlipayApiException;
import com.atguigu.common.constant.order.PaymentConstant;
import com.atguigu.common.vo.order.PayAsyncVO;
import com.atguigu.common.vo.order.PayVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 策略角色接口
 */
public interface PayStrategy {

    /**
     * 创建交易付款
     * @param order 订单详情
     */
    String pay(PayVO order) throws Exception;

    /**
     * 处理回调
     * @param request   回调参数
     * @param asyncVo   回调VO参数
     */
    Boolean notify(HttpServletRequest request, PayAsyncVO asyncVo) throws AlipayApiException;
}