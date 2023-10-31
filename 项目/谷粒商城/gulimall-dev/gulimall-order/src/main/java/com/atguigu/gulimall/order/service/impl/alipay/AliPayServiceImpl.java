package com.atguigu.gulimall.order.service.impl.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.common.constant.order.OrderConstant.OrderStatusEnum;
import com.atguigu.common.entity.order.PaymentInfoEntity;
import com.atguigu.common.vo.order.PayAsyncVO;
import com.atguigu.common.vo.order.PayVO;
import com.atguigu.common.vo.order.alipay.AliPayAsyncVO;
import com.atguigu.gulimall.order.config.AliPayConfig;
import com.atguigu.gulimall.order.service.PayService;
import com.atguigu.gulimall.order.service.impl.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付
 *
 * @Author: wanzenghui
 * @Date: 2022/1/4 23:19
 */
@Service
public class AliPayServiceImpl implements PayService {

    @Autowired
    OrderServiceImpl orderService;
    @Autowired
    AliPayConfig aliPayConfig;

    /**
     * 创建支付，获取支付页
     */
    @Override
    public String pay(PayVO order) throws AlipayApiException {
        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
                aliPayConfig.getGatewayUrl(),
                aliPayConfig.getApp_id(),
                aliPayConfig.getMerchant_private_key(),
                "json",
                aliPayConfig.getCharset(),
                aliPayConfig.getAlipay_public_key(),
                aliPayConfig.getSign_type());

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(order.getReturn_url());
        alipayRequest.setNotifyUrl(order.getNotify_url());

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = order.getOut_trade_no();
        //付款金额，必填
        String total_amount = order.getTotal_amount();
        //订单名称，必填
        String subject = order.getSubject();
        //商品描述，可空
        String body = order.getBody();
        //超时时间
        String timeout = aliPayConfig.getTimeout();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout + "\","// TODO 使用绝对时间：time_expire
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        // 执行创建支付请求，返回支付页面
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝响应：登录页面的代码\n" + result);

        return result;
    }

    /**
     * 验签
     *
     * @param request 回参
     */
    @Override
    public Boolean verify(HttpServletRequest request) throws AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        return AlipaySignature.rsaCheckV1(params, aliPayConfig.getAlipay_public_key(),
                aliPayConfig.getCharset(), aliPayConfig.getSign_type()); //调用SDK验证签名
    }

    /**
     * 处理支付回调
     *
     * @param asyncVo
     */
    @Override
    public void handlePayResult(PayAsyncVO asyncVo) {
        // 封装交易流水信息
        AliPayAsyncVO aliVo = (AliPayAsyncVO) asyncVo;
        // 保存交易流水信息
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(aliVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(aliVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(aliVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(aliVo.getBody());
        paymentInfo.setPaymentStatus(aliVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(aliVo.getNotify_time());

        // 获取支付状态
        String tradeStatus = aliVo.getTrade_status();
        Integer orderStatus = null;
        if (tradeStatus.equals("TRADE_SUCCESS") || tradeStatus.equals("TRADE_FINISHED")) {
            // 支付成功状态
            orderStatus = OrderStatusEnum.PAYED.getCode();
        }
        orderService.handlePayResult(orderStatus, aliVo.getPayCode(), paymentInfo);
    }
}