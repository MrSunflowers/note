package com.atguigu.common.to.product;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品信息TO
 */
@Data
public class SkuInfoTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long skuId;
    private String skuName;

}
