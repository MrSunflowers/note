package com.atguigu.gulimall.ware.dao;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 商品库存
 * 
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:59:35
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 采购成功，商品入库
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 查询sku是否有库存
     */
    Long getSkusStock(Long skuId);

    /**
     * 查询商品库存充足的仓库
     * @param skuIds 商品项ID集合
     * @return
     */
    List<WareSkuEntity> selectListHasSkuStock(@Param("skuIds") Set<Long> skuIds);

    /**
     * 锁定库存
     * @param skuId 商品项ID
     * @param wareId 仓库ID
     * @param count 待锁定库存数
     * @return 1成功  0失败
     */
    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

    /**
     * 解锁库存
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    void unLockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);
}
