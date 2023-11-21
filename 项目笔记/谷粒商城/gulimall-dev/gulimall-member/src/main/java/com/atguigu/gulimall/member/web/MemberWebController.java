package com.atguigu.gulimall.member.web;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付同步回调
 *
 * @Author: wanzenghui
 * @Date: 2022/1/5 0:17
 */
@Controller
public class MemberWebController {

    @Autowired
    private OrderFeignService orderFeignService;

    /**
     * 支付宝同步回调
     * 查询用户订单列表
     * @param pageNum
     * @param model
     * @return
     */
    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                  Model model) {
        // 获取支付宝回参，根据sign延签，延签成功修改订单状态【不建议在同步回调修改订单状态，建议在异步回调修改订单状态】

        // 封装分页数据
        Map<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());

        // 分页查询当前用户的订单列表、订单项
        R orderInfo = orderFeignService.listWithItem(page);
        model.addAttribute("orders", orderInfo);

        return "orderList";
    }

}