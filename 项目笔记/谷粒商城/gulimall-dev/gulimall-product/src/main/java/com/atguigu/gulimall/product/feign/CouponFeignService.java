package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.product.SkuReductionTO;
import com.atguigu.common.to.product.SpuBoundTO;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    /**
     * 新增积分信息（当前spu商品购买新增的积分规则信息）
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTO boundTo);

    /**
     * 新增满减信息
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTO reductionTo);
}
