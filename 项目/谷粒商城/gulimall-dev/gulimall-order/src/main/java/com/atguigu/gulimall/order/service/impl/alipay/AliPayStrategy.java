package com.atguigu.gulimall.order.service.impl.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.common.constant.order.PaymentConstant;
import com.atguigu.common.vo.order.PayAsyncVO;
import com.atguigu.common.vo.order.PayVO;
import com.atguigu.gulimall.order.config.AliPayConfig;
import com.atguigu.gulimall.order.service.PayStrategy;
import com.atguigu.gulimall.order.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝策略角色实现
 */
@Component
public class AliPayStrategy implements PayStrategy {

    @Autowired
    AliPayServiceImpl aliPayService;

    /**
     * 创建支付
     * @param order 订单详情
     */
    @Override
    @Transactional
    public String pay(PayVO order) throws AlipayApiException {
        // 创建支付返回html渲染
        String html = aliPayService.pay(order);
        return html;
    }

    @Override
    public Boolean notify(HttpServletRequest request, PayAsyncVO asyncVo) throws AlipayApiException {
        // 验签
        Boolean signVerified = aliPayService.verify(request);
        if (signVerified) {
            // 修改订单状态
            System.out.println("签名验证成功...修改订单状态");
            aliPayService.handlePayResult(asyncVo);
        } else {
            System.out.println("签名验证失败...");
        }
        return signVerified;
    }
}