package com.atguigu.gulimall.cart.web;

import com.atguigu.common.to.cart.UserInfoTO;
import com.atguigu.common.vo.cart.CartItemVO;
import com.atguigu.common.vo.cart.CartVO;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.impl.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 购物车页面相关操作控制器
 * @Author: wanzenghui
 * @Date: 2021/12/5 0:13
 */
@Controller
public class CartController {

    @Autowired
    CartServiceImpl cartService;

    /**
     * 添加商品到购物车
     *
     * @param skuId      商品ID
     * @param num        商品数量
     * @param attributes 重定向数据域
     */
    @GetMapping(value = "/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num,
                              RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        // 添加sku商品到购物车
        cartService.addToCart(skuId, num);
        attributes.addAttribute("skuId", skuId);// 会在url后面拼接参数
        // 请求重定向给addToCartSuccessPage.html，防刷
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }

    /**
     * 商品添加购物车成功页（防刷）
     */
    @GetMapping(value = "/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model) {
        //重定向到成功页面。再次查询购物车数据即可
        CartItemVO cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "success";
    }

    /**
     * 购物车列表页
     * 1.拦截器封装用户信息
     * 1）已登录状态：封装userId+userKey到ThreadLocal中
     * 2）未登录状态：
     * 2-1）已分配游客标识，封装userKey到ThreadLocal中
     * 2-2）未分配游客标识，命令客户端保存cookie（user-key），并封装userKey到ThreadLocal中
     * 2.根据用户标识获取购物车信息
     * 1）已登录状态
     * 使用userId作为key获取购物车
     * 使用userKey作为key获取游客购物车，如果非空则与用户购物车合并
     * 2）未登录状态
     * 使用userKey作为key获取游客购物车
     * 3.返回cartList列表页
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        CartVO cartVO = cartService.getCart();
        model.addAttribute("cart", cartVO);
        return "cartList";
    }

    /**
     * 更改购物车商品选中状态
     */
    @GetMapping(value = "/checkItem")
    public String checkItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "checked") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 改变商品数量
     */
    @GetMapping(value = "/countItem")
    public String countItem(@RequestParam(value = "skuId") Long skuId,
                            @RequestParam(value = "num") Integer num) {
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 删除商品信息
     */
    @GetMapping(value = "/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId) {
        cartService.deleteIdCartInfo(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

}