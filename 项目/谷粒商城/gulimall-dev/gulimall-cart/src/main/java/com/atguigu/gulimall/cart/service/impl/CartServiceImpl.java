package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ObjectConstant;
import com.atguigu.common.constant.cart.CartConstant;
import com.atguigu.common.to.cart.UserInfoTO;
import com.atguigu.common.utils.DateUtils;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.cart.CartItemVO;
import com.atguigu.common.vo.cart.CartVO;
import com.atguigu.common.vo.cart.SkuInfoVO;
import com.atguigu.gulimall.cart.exception.CartExceptionHandler;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 购物车
 *
 * @Author: wanzenghui
 * @Date: 2021/12/4 23:54
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 添加sku商品到购物车
     */
    @Override
    public CartItemVO addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 获取购物车redis操作对象
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        // 获取商品
        String cartItemJSONString = (String) operations.get(skuId.toString());
        if (StringUtils.isEmpty(cartItemJSONString)) {
            // 购物车不存在此商品，需要将当前商品添加到购物车中
            CartItemVO cartItem = new CartItemVO();
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                // 远程查询当前商品信息
                R r = productFeignService.getInfo(skuId);
                SkuInfoVO skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVO>() {
                });
                cartItem.setSkuId(skuInfo.getSkuId());// 商品ID
                cartItem.setTitle(skuInfo.getSkuTitle());// 商品标题
                cartItem.setImage(skuInfo.getSkuDefaultImg());// 商品默认图片
                cartItem.setPrice(skuInfo.getPrice());// 商品单价
                cartItem.setCount(num);// 商品件数
                cartItem.setCheck(true);// 是否选中
            }, executor);

            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                // 远程查询attrName:attrValue信息
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttrValues(skuSaleAttrValues);
            }, executor);

            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();
            operations.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        } else {
            // 当前购物车已存在此商品，修改当前商品数量
            CartItemVO cartItem = JSON.parseObject(cartItemJSONString, CartItemVO.class);
            cartItem.setCount(cartItem.getCount() + num);
            operations.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    /**
     * 根据skuId获取购物车商品信息
     */
    @Override
    public CartItemVO getCartItem(Long skuId) {
        // 获取购物车redis操作对象
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItemJSONString = (String) cartOps.get(skuId.toString());
        CartItemVO cartItemVo = JSON.parseObject(cartItemJSONString, CartItemVO.class);
        return cartItemVo;
    }

    /**
     * 根据用户信息获取购物车redis操作对象
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 获取用户登录信息
        UserInfoTO userInfo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfo.getUserId() != null) {
            // 登录态，使用用户购物车
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserId();
        } else {
            // 非登录态，使用游客购物车
            cartKey = CartConstant.CART_PREFIX + userInfo.getUserKey();
        }
        // 绑定购物车的key操作Redis
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }


    /**
     * 获取购物车列表
     */
    @Override
    public CartVO getCart() throws ExecutionException, InterruptedException {
        CartVO cart = new CartVO();
        // 获取用户登录信息
        UserInfoTO userInfo = CartInterceptor.threadLocal.get();
        // 获取游客购物车
        List<CartItemVO> touristItems = getCartItems(CartConstant.CART_PREFIX + userInfo.getUserKey());
        if (userInfo.getUserId() != null) {
            // 登录状态
            if (!CollectionUtils.isEmpty(touristItems)) {
                // 游客购物车非空，需要整合到用户购物车
                for (CartItemVO item : touristItems) {
                    // 将商品逐个放到用户购物车
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 清楚游客购物车
                clearCart(CartConstant.CART_PREFIX + userInfo.getUserKey());
            }
            // 获取用户购物车（已经合并后的购物车）
            List<CartItemVO> items = getCartItems(CartConstant.CART_PREFIX + userInfo.getUserId());
            cart.setItems(items);
        } else {
            // 未登录状态，返回游客购物车
            cart.setItems(touristItems);
        }
        return cart;
    }

    /**
     * 根据购物车的key获取
     */
    private List<CartItemVO> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (!CollectionUtils.isEmpty(values)) {
            // 购物车非空，反序列化成商品并封装成集合返回
            return values.stream()
                    .map(jsonString -> JSONObject.parseObject((String) jsonString, CartItemVO.class))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    /**
     * 更改购物车商品选中状态
     */
    @Override
    public void checkItem(Long skuId, Integer check) {
        // 查询购物车商品信息
        CartItemVO cartItem = getCartItem(skuId);
        // 修改商品选中状态
        cartItem.setCheck(ObjectConstant.BooleanIntEnum.YES.getCode().equals(check) ? true : false);
        // 更新到redis中
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.put(skuId.toString(), JSONObject.toJSONStringWithDateFormat(cartItem, DateUtils.DATATIMEF_TIME_STR));
    }

    /**
     * 改变商品数量
     */
    @Override
    public void changeItemCount(Long skuId, Integer num) {
        // 查询购物车商品信息
        CartItemVO cartItem = getCartItem(skuId);
        // 修改商品数量
        cartItem.setCount(num);
        // 更新到redis中
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.put(skuId.toString(), JSONObject.toJSONStringWithDateFormat(cartItem, DateUtils.DATATIMEF_TIME_STR));
    }

    /**
     * 删除购物项
     */
    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> operations = getCartOps();
        operations.delete(skuId.toString());
    }

    /**
     * 获取当前用户的购物车所有选中的商品项
     *  1.从redis中获取所有选中的商品项
     *  2.获取mysql最新的商品价格信息，替换redis中的价格信息
     */
    @Override
    public List<CartItemVO> getUserCartItems() {
        // 获取当前用户登录的信息
        UserInfoTO userInfo = CartInterceptor.threadLocal.get();
        if (userInfo.getUserId() == null) {
            // 未登录
            return null;
        } else {
            // 已登录，查询redis用户购物车
            List<CartItemVO> items = getCartItems(CartConstant.CART_PREFIX + userInfo.getUserId());
            if (CollectionUtils.isEmpty(items)) {
                throw new CartExceptionHandler();
            }
            // 筛选所有选中的sku
            Map<Long, CartItemVO> itemMap = items.stream().filter(item -> item.getCheck())
                    .collect(Collectors.toMap(CartItemVO::getSkuId, val -> val));
            // 调用远程获取最新价格
            Map<Long, BigDecimal> priceMap = productFeignService.getPrice(itemMap.keySet());
            // 遍历封装真实价格返回
            return itemMap.entrySet().stream().map(entry -> {
                CartItemVO item = entry.getValue();
                item.setPrice(priceMap.get(entry.getKey()));// 封装真实价格
                return item;
            }).collect(Collectors.toList());
        }
    }
}