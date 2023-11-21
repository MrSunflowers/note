package com.atguigu.gulimall.order.web;

import com.atguigu.common.constant.order.PaymentConstant;
import com.atguigu.common.constant.order.PaymentConstant.PayBusinessDetailType;
import com.atguigu.common.constant.order.PaymentConstant.PayType;
import com.atguigu.common.vo.order.PayVO;
import com.atguigu.gulimall.order.config.AliPayConfig;
import com.atguigu.gulimall.order.service.impl.OrderServiceImpl;
import com.atguigu.gulimall.order.service.impl.PayContextStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: wan
 */
@Slf4j
@Controller
public class PayWebController {

    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    PayContextStrategy payContextStrategy;
    @Autowired
    AliPayConfig aliPayConfig;

    /**
     * 创建支付
     * 返回text/html页面
     * @param orderSn       订单号
     * @param type          支付类型
     * @param businessType  业务类型
     */
    @ResponseBody
    @GetMapping(value = "/html/pay", produces = "text/html")
    public String htmlPayOrder(@RequestParam(value = "orderSn", required = false) String orderSn,
                               @RequestParam(value = "payCode", required = true) Integer payCode,
                               @RequestParam(value = "businessCode", required = true) Integer businessCode) throws Exception {
        // 获取支付类型
        PayType payType = PayType.getByCode(payCode);
        // 获取业务类型
        PayBusinessDetailType businessDetailType = PayBusinessDetailType.getByCodeAndBusinessCode(
                payType.getCode(), businessCode);

        // 获取订单信息，构造参数
        PayVO order = orderService.getOrderPay(orderSn);
        order.setNotify_url(PaymentConstant.SYSTEM_URL + businessDetailType.getNotifyUrl());// 封装异步回调地址
        order.setReturn_url(businessDetailType.getReturnUrl());// 封装同步回调地址

        // 请求策略方法
        String html = payContextStrategy.pay(payType, order);
        return html;
    }


}
