package com.atguigu.gulimall.search.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: wanzenghui
 * @Date: 2021/11/15 21:58
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 查询属性
     */
    @GetMapping("/product/attr/info/{attrId}}")
    R attrInfo(@PathVariable("attrId") Long attrId);
}