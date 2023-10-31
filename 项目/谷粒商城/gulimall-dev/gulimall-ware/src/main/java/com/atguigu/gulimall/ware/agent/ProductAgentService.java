package com.atguigu.gulimall.ware.agent;

import com.atguigu.common.to.product.SkuInfoTO;
import com.atguigu.common.utils.ObjectUtil;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ProductAgentService {

    @Autowired
    ProductFeignService productFeignService;

    /**
     * 查询skuInfo
     * @param skuId
     * @return
     */
    public SkuInfoTO info(Long skuId) {
        // TODO 远程查询，查询失败不回滚
        // 方法一：try catch
        // 方法二：高级篇
        try {
            R info = productFeignService.info(skuId);
            if (info.getCode() == 0) {
                // 获取成功
                Map map = (Map) info.get("skuInfo");
                SkuInfoTO sku = new SkuInfoTO();
                ObjectUtil.copyMap2Bean(map, sku);
                return sku;
            }
        } catch (Exception e) {
            log.error("feign调用查询skuInfo异常{}" + e.getMessage());
        }
        return null;
    }
}
