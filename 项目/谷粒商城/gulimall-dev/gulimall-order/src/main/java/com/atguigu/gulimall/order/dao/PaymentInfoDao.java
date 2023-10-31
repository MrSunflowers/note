package com.atguigu.gulimall.order.dao;

import com.atguigu.common.entity.order.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:57:46
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
