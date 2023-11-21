package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.product.SpuConstant;
import com.atguigu.common.entity.product.*;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.to.ware.SkuHasStockTO;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.product.BaseAttrs;
import com.atguigu.common.vo.product.Bounds;
import com.atguigu.common.vo.product.Skus;
import com.atguigu.common.vo.product.SpuSaveVO;
import com.atguigu.gulimall.product.agent.CouponAgentService;
import com.atguigu.gulimall.product.agent.SearchAgentService;
import com.atguigu.gulimall.product.agent.WareAgentService;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    CouponAgentService couponAgentService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrService attrService;
    @Autowired
    WareAgentService wareAgentService;
    @Autowired
    SearchAgentService searchAgentService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 发布商品
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVO vo) {
        // 1.保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        // MetaObjectHandler处理时间
        BeanUtils.copyProperties(vo, spuInfo);
        Date now = new Date();
        spuInfo.setCreateTime(now);
        spuInfo.setUpdateTime(now);
        this.saveBaseSpuInfo(spuInfo);
        Long spuId = spuInfo.getId();

        // 2.保存spu描述图片（商品介绍里面的图） pms_spu_info_desc
        List<String> decript = vo.getDecript();
        spuInfoDescService.saveSpuInfoDesc(spuId, decript);

        // 3.保存spu图片集（商品展示图） pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveSpuImages(spuId, images);

        // 4.保存spu基本参数值 pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        productAttrValueService.saveProductAttrValue(spuId, baseAttrs);

        // 5.保存spu的积分信息（购买产生积分，现阶段绑定spu，可以绑定sku） sms_spu_bounds
        Bounds bounds = vo.getBounds();
        R r = couponAgentService.saveSpuBounds(spuId, bounds);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        // 6.保存当前spu对应的所有sku信息
        //   6.1)sku的基本信息：pms_sku_info
        //   6.2)sku的图片信息：pms_sku_images
        //   6.3)sku的销售属性值：pms_sku_sale_attr_value
        //   6.4)sku的打折（买几件打几折）、满减信息（满多少减多少）、会员价格：
        //   sms_sku_ladder\sms_sku_full_reduction\sms_member_price
        List<Skus> skus = vo.getSkus();
        skuInfoService.saveSkuInfo(spuInfo, skus);

    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfo) {
        this.baseMapper.insert(spuInfo);
    }

    /**
     * sup商品上架
     * 往es上架商品不会重复上架，因为上架时指定了skuId
     */
    @Override
    public void up(Long spuId) {
        // 1.查询spuId对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

        // 2.封装待上传es数据集合（skuEsModel）
        // 查询spu关联的基本属性集合
        Map<Long, ProductAttrValueEntity> attrMap = productAttrValueService.baseAttrlistforspu(spuId)
                .stream().collect(Collectors.toMap(key -> key.getAttrId(), val -> val));
        // 查询允许被检索的基本属性集合ID
        List<Long> searAttrIds = attrService.selectSearchAttrIds(new ArrayList<>(attrMap.keySet()));
        // 查询允许被检索的基本属性属性集合，并封装成attrEsModels
        List<SkuEsModel.Attrs> attrEsModels = searAttrIds.stream().map(attrId -> {
            SkuEsModel.Attrs attrModel = new SkuEsModel.Attrs();
            ProductAttrValueEntity attrValue = attrMap.get(attrId);
            // 封装基本属性
            attrModel.setAttrId(attrValue.getAttrId());
            attrModel.setAttrName(attrValue.getAttrName());
            attrModel.setAttrValue(attrValue.getAttrValue());
            return attrModel;
        }).collect(Collectors.toList());

        Map<Long, Boolean> skuHasStockMap = null;
        try {
            // 查询库存
            List<Long> skuIds = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            skuHasStockMap = wareAgentService.getSkusHasStock(skuIds).stream()
                    .collect(Collectors.toMap(SkuHasStockTO::getSkuId, val -> val.getHasStock()));
        } catch (Exception e) {
            log.error("库存查询异常：原因{}", e);
        }
        Map<Long, Boolean> finalSkuHasStockMap = skuHasStockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            // 3.遍历sku封装SkuEsModel
            SkuEsModel esModel = new SkuEsModel();
            // 封装属性名相同的属性值
            BeanUtils.copyProperties(sku, esModel);
            // 封装属性名不相同的属性值 skuPrice, skuImg
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            // 封装库存（远程调用失败，默认设置有库存）
            esModel.setHasStock(finalSkuHasStockMap == null ? true : finalSkuHasStockMap.get(sku.getSkuId()));

            // TODO 热度评分 hotScore（这里直接设置0，待扩展）
            esModel.setHotScore(0L);

            // 封装品牌和分类的名字
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(category.getName());

            // 封装允许被检索的基本属性值（多条sku冗余）
            esModel.setAttrs(attrEsModels);
            return esModel;
        }).collect(Collectors.toList());

        // 4.调用es模块保存数据
        boolean result = searchAgentService.productStatusUp(upProducts);
        if (result) {
            // 上架成功
            // 5.修改sku商品状态，上架状态
            baseMapper.updateSpuStatus(spuId, SpuConstant.PublishStatusEnum.SPU_UP.getCode());
        } else {
            // 上架失败
            // TODO 7.重复调用，接口幂等性（重试机制）
            
        }

    }

    /**
     * 根据SkuId查询SPU信息
     */
    @Override
    public SpuInfoEntity getBySkuId(Long skuId) {
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        SpuInfoEntity spuInfo = getById(skuInfo.getSpuId());
        BrandEntity brand = brandService.getById(spuInfo.getBrandId());
        spuInfo.setBrandName(brand.getName());
        return spuInfo;
    }
}