package com.atguigu.gulimall.order.service;

import com.atguigu.common.entity.order.PaymentInfoEntity;
import com.atguigu.common.to.mq.SeckillOrderTO;
import com.atguigu.common.vo.order.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.entity.order.OrderEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:57:46
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 分页查询订单列表、订单详情
     * @param params
     * @return
     */
    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     * 获取订单详情
     */
    OrderEntity getOrderByOrderSn(String orderSn);

    /**
     * 获取结算页（confirm.html）VO数据
     */
    OrderConfirmVO OrderConfirmVO() throws Exception;

    /**
     * 创建订单
     */
    SubmitOrderResponseVO submitOrder(OrderSubmitVO vo) throws Exception;

    /**
     * 关闭订单
     */
    void closeOrder(OrderEntity order);

    /**
     * 获取订单支付的详细信息
     */
    PayVO getOrderPay(String orderSn);

    /**
     * 处理支付回调
     * @param targetOrderStatus 目标状态
     */
    void handlePayResult(Integer targetOrderStatus, Integer payCode, PaymentInfoEntity paymentInfo);

    /**
     * 创建秒杀订单
     * @param order 秒杀订单信息
     */
    void createSeckillOrder(SeckillOrderTO order);
}

