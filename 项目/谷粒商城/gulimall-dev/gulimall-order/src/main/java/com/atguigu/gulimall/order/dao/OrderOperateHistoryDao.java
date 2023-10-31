package com.atguigu.gulimall.order.dao;

import com.atguigu.common.entity.order.OrderOperateHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单操作历史记录
 * 
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:57:46
 */
@Mapper
public interface OrderOperateHistoryDao extends BaseMapper<OrderOperateHistoryEntity> {
	
}
