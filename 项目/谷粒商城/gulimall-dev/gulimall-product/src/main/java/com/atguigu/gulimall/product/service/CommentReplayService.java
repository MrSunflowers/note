package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.entity.product.CommentReplayEntity;

import java.util.Map;

/**
 * 商品评价回复关系
 *
 * @author wanzenghui
 * @email lemon_wan@aliyun.com
 * @date 2021-09-02 22:58:35
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

