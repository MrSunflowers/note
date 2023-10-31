package com.atguigu.gulimall.product.agent;

import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchAgentService {

    @Autowired
    SearchFeignService searchFeignService;

    /**
     * 上架商品
     */
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) {
        R result = searchFeignService.productStatusUp(skuEsModels);
        return result.getCode() == 0 ? true : false;
    }


}
