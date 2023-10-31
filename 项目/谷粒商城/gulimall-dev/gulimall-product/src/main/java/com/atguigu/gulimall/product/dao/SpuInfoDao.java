package com.atguigu.gulimall.product.dao;

import com.atguigu.common.entity.product.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 修改商品状态
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
