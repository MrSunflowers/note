/**
  * Copyright 2020 bejson.com 
  */
package com.atguigu.common.vo.product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2020-08-12 20:18:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SpuSaveVO {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;// 保存spu描述图片
    private List<String> images;// 保存spu图片集
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;// 保存spu基本参数
    private List<Skus> skus;//

}