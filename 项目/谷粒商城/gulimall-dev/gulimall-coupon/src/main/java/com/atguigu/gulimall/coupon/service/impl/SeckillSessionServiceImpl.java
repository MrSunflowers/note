package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.utils.DateUtils;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.coupon.dao.SeckillSessionDao;
import com.atguigu.gulimall.coupon.entity.SeckillSessionEntity;
import com.atguigu.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.atguigu.gulimall.coupon.service.SeckillSessionService;
import com.atguigu.gulimall.coupon.service.SeckillSkuRelationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationServiceImpl seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询最近三天需要秒杀的场次+商品
     */
    @Override
    public List<SeckillSessionEntity> getLates3DaySession() {
        // 计算最近三天起止时间
        String startTime = DateUtils.currentStartTime();// 当天00:00:00
        String endTime = DateUtils.getTimeByOfferset(2);// 后天23:59:59

        // 查询起止时间内的秒杀场次
        List<SeckillSessionEntity> sessions = baseMapper.selectList(new QueryWrapper<SeckillSessionEntity>()
                .between("start_time", startTime, endTime));

        // 组合秒杀关联的商品信息
        if (!CollectionUtils.isEmpty(sessions)) {
            // 组合场次ID
            List<Long> sessionIds = sessions.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());
            // 查询秒杀场次关联商品信息
            Map<Long, List<SeckillSkuRelationEntity>> skuMap = seckillSkuRelationService
                    .list(new QueryWrapper<SeckillSkuRelationEntity>().in("promotion_session_id", sessionIds))
                    .stream().collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionSessionId));
            sessions.forEach(session -> session.setRelationSkus(skuMap.get(session.getId())));
        }
        return sessions;
    }


}