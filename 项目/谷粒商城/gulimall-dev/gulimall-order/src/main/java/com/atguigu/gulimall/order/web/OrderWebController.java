package com.atguigu.gulimall.order.web;

import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.exception.VerifyPriceException;
import com.atguigu.common.vo.order.OrderConfirmVO;
import com.atguigu.common.vo.order.OrderSubmitVO;
import com.atguigu.common.vo.order.SubmitOrderResponseVO;
import com.atguigu.gulimall.order.annotation.TokenVerify;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

/**
 * @Author: wanzenghui
 * @Date: 2021/12/20 21:59
 */
@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    /**
     * 跳转结算页
     * 购物车页cart.html点击去结算跳转confirm.html结算页
     */
    @GetMapping(value = "/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        // 查询结算页VO
        OrderConfirmVO confirmVo = null;
        try {
            confirmVo = orderService.OrderConfirmVO();
            model.addAttribute("confirmOrderData", confirmVo);
            // 跳转结算页
            return "confirm";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "toTrade";
    }

    /**
     * 创建订单
     * 创建成功，跳转订单支付页
     * 创建失败，跳转结算页
     * 无需提交要购买的商品，提交订单时会实时查询最新的购物车商品选中数据提交
     */
    @TokenVerify
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVO vo, Model model, RedirectAttributes attributes) {
        try {
            SubmitOrderResponseVO orderVO = orderService.submitOrder(vo);
            // 创建订单成功，跳转收银台
            model.addAttribute("submitOrderResp", orderVO);// 封装VO订单数据，供页面解析[订单号、应付金额]
            return "pay";
        } catch (Exception e) {
            // 下单失败回到订单结算页
            if (e instanceof VerifyPriceException) {
                String message = ((VerifyPriceException) e).getMessage();
                attributes.addFlashAttribute("msg", "下单失败" + message);
            } else if (e instanceof NoStockException) {
                String message = ((NoStockException) e).getMessage();
                attributes.addFlashAttribute("msg", "下单失败" + message);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }
}