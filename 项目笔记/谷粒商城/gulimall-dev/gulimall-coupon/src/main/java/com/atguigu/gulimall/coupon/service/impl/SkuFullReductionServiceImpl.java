package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.to.product.MemberPrice;
import com.atguigu.common.to.product.SkuReductionTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    SkuFullReductionService skuFullReductionService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 新增满减信息（发布商品）
     */
    @Override
    public void saveSkuReduction(SkuReductionTO reductionTo) {
        // 1.sku的打折（买几件打几折）sms_sku_ladder【剔除满减信息为0的】
        if (reductionTo.getFullCount() > 0) {
            SkuLadderEntity skuLadder = new SkuLadderEntity();
            BeanUtils.copyProperties(reductionTo, skuLadder);
            skuLadder.setAddOther(reductionTo.getCountStatus());
            skuLadderService.save(skuLadder);
        }

        // 2.满减信息（满多少减多少）sms_sku_full_reduction【剔除满减信息为0的】
        if (reductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
            SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
            BeanUtils.copyProperties(reductionTo, skuFullReduction);
            skuFullReduction.setAddOther(reductionTo.getPriceStatus());
            this.save(skuFullReduction);
        }

        // 3.会员价格：sms_member_price【剔除会员价格为0的数据】
        List<MemberPrice> memberPrices = reductionTo.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntities = memberPrices.stream().
                filter(memberPriceVo -> memberPriceVo.getPrice().compareTo(BigDecimal.ZERO) == 1).
                map(memberPriceVo -> {
                    MemberPriceEntity memberPrice = new MemberPriceEntity();
                    memberPrice.setSkuId(reductionTo.getSkuId());
                    memberPrice.setMemberLevelId(memberPriceVo.getId());
                    memberPrice.setMemberLevelName(memberPriceVo.getName());
                    memberPrice.setMemberPrice(memberPriceVo.getPrice());
                    memberPrice.setAddOther(1);

                    return memberPrice;
                }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(memberPriceEntities)) {
            memberPriceService.saveBatch(memberPriceEntities);
        }
    }
}