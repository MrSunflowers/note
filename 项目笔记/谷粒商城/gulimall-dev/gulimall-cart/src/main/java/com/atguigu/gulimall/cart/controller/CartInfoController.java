package com.atguigu.gulimall.cart.controller;

import com.atguigu.common.vo.cart.CartItemVO;
import com.atguigu.gulimall.cart.service.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 购物车信息控制器
 * @Author: wanzenghui
 * @Date: 2021/12/8 23:54
 */
@RestController
public class CartInfoController {

    @Autowired
    CartServiceImpl cartService;

    /**
     * 获取当前用户的购物车所有选中的商品项
     *  1.从redis中获取所有选中的商品项
     *  2.获取mysql最新的商品价格信息，替换redis中的价格信息
     */
    @GetMapping(value = "/currentUserCartItems")
    @ResponseBody
    public List<CartItemVO> getCurrentCartItems() {
        return cartService.getUserCartItems();
    }
}