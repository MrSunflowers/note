package com.atguigu.gulimall.cart.service;

import com.atguigu.common.vo.cart.CartItemVO;
import com.atguigu.common.vo.cart.CartVO;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车
 * @Author: wanzenghui
 * @Date: 2021/12/4 23:53
 */
public interface CartService {

    /**
     * 添加sku商品到购物车
     */
    CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 根据skuId获取购物车商品信息
     */
    CartItemVO getCartItem(Long skuId);

    /**
     * 获取购物车列表
     */
    CartVO getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车
     */
    void clearCart(String cartKey);

    /**
     * 更改购物车商品选中状态
     */
    void checkItem(Long skuId, Integer check);

    /**
     * 改变商品数量
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     */
    void deleteIdCartInfo(Integer skuId);

    /**
     * 获取当前用户的购物车所有选中的商品项
     *  1.从redis中获取所有选中的商品项
     *  2.获取mysql最新的商品价格信息，替换redis中的价格信息
     */
    List<CartItemVO> getUserCartItems();
}