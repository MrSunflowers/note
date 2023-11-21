package com.atguigu.common.to.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 积分，成长值
 */
@Data
public class SpuBoundTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
