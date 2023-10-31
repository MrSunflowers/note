package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.constant.search.EsConstant;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.vo.search.SearchParam;
import com.atguigu.common.vo.search.SearchResult;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.service.MallSearchService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.util.UriEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商城检索
 *
 * @Author: wanzenghui
 * @Date: 2021/11/10 23:51
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    RestHighLevelClient client;

    /**
     * 检索商品
     */
    @Override
    public SearchResult search(SearchParam param) {
        // 1.准备检索请求，动态构建DSL语句
        SearchRequest searchRequest = buildSearchRequest(param);

        SearchResult result = null;// 结果
        try {
            // 2.执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);

            // 3.分析相应数据封装result返回
            result = buildSearchResuult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 动态构建检索请求
     * 模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存），排序，分页，高亮 ,聚合分析【分析所有可选的规格、分类、品牌】
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        // 构建SourceBuilder【通过builder构建DSL语句】
        SearchSourceBuilder builder = new SearchSourceBuilder();

        // 动态构建查询DSL语句【参照dsl.json分析包装步骤】
        // 查询：模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
        // 1.构建bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1.1.构建must（模糊查询）
        if (StringUtils.isNotBlank(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2.构建filter（过滤）
        // 1.2.1.三级分类
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.2.2.品牌id
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 1.2.3.属性
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            for (String attr : param.getAttrs()) {
                // attrs=1_白色:蓝色
                String[] attrs = attr.split("_");
                String attrId = attrs[0];// 1
                String[] attrValues = attrs[1].split(":");// ["白色","蓝色"]
                BoolQueryBuilder nestedBoolQueryBuilder = QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("attrs.attrId", attrId))
                        .must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                // 遍历每一个属性生成一个NestedQuery
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQueryBuilder, ScoreMode.None);// ScoreMode.None：不参与评分
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        // 1.2.4.库存
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }

        // 1.2.5.价格区间【多种区间传参方式：1_500/_500/500_】
        if (StringUtils.isNotBlank(param.getSkuPrice())) {
            String[] prices = param.getSkuPrice().split("_");
            if (prices.length == 2) {
                // 1_500
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").gte(prices[0]).lte(prices[1]));
            } else if (prices.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    // _500
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").lte(prices[0]));
                } else if (param.getSkuPrice().endsWith("_")) {
                    // 500_
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").gte(prices[0]));
                }
            }
        }

        // 1.3.封装bool【bool封装了模糊查询+过滤】
        builder.query(boolQueryBuilder);

        // 1.4.排序，分页，高亮
        // 1.4.1.排序
        if (StringUtils.isNotBlank(param.getSort())) {
            String[] sorts = param.getSort().split("_");
            builder.sort(sorts[0], sorts[1].toLowerCase().equals(SortOrder.ASC.toString()) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 1.4.2.分页
        builder.from(EsConstant.PRODUCT_PAGESIZE * (param.getPageNum() - 1));
        builder.size(EsConstant.PRODUCT_PAGESIZE);
        // 1.4.3.高亮
        if (StringUtils.isNotBlank(param.getKeyword())) {
            // 模糊匹配才需要高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle").preTags("<b style='color:red'>").postTags("</b>");
            builder.highlighter(highlightBuilder);
        }

        // 1.5.聚合分析【分析所有可选的规格、分类、品牌】
        // 1.5.1.品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // 品牌聚合子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        builder.aggregation(brandAgg);

        // 1.5.2.分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        builder.aggregation(catalogAgg);

        // 1.5.3.属性嵌套聚合
        NestedAggregationBuilder attrNestedAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 属性子聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        attrNestedAgg.subAggregation(attrIdAgg);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        builder.aggregation(attrNestedAgg);

        System.out.println("构建的DSL语句: " + builder.toString());
        // 根据构建了DSL语句的builder创建检索请求对象
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, builder);
        return searchRequest;
    }

    /**
     * 封装检索结果
     * 1、返回所有查询到的商品
     * 2、分页信息
     * 3、当前所有商品涉及到的所有属性信息
     * 4、当前所有商品涉及到的所有品牌信息
     * 5、当前所有商品涉及到的所有分类信息
     */
    private SearchResult buildSearchResuult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        // ==========从命中结果获取===========hits
        SearchHits hits = response.getHits();// 获取命中结果
        // 1.返回所有查询到的商品
        List<SkuEsModel> products = new ArrayList<>();
        if (!ArrayUtils.isEmpty(hits.getHits())) {
            for (SearchHit hit : hits.getHits()) {
                String jsonString = hit.getSourceAsString();// 获取jsonString
                SkuEsModel esModel = JSONObject.parseObject(jsonString, SkuEsModel.class);
                if (StringUtils.isNotBlank(param.getKeyword())) {
                    // 关键字不为空，返回结果包含高亮信息
                    // 高亮信息
                    String skuTitle = hit.getHighlightFields().get("skuTitle").fragments()[0].string();
                    esModel.setSkuTitle(skuTitle);
                }
                products.add(esModel);
            }
        }
        result.setProducts(products);
        // 2.分页信息
        long total = hits.getTotalHits().value;
        long totalPages = total % EsConstant.PRODUCT_PAGESIZE == 0 ? total / EsConstant.PRODUCT_PAGESIZE : total / EsConstant.PRODUCT_PAGESIZE + 1;
        result.setPageNum(param.getPageNum());// 当前页码
        result.setTotal(total);// 总记录数
        result.setTotalPages((int) totalPages);// 总页码
        // 导航页码
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        // ==========从聚合结果获取===========aggregations
        Aggregations aggregations = response.getAggregations();// 获取聚合结果
        // 3.当前所有商品涉及到的所有属性信息
        ArrayList<SearchResult.AttrVo> attrs = new ArrayList<>();
        ParsedNested attrAgg = aggregations.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        Map<Long, String> attrMap = new HashMap<>();// 面包屑map数据源【属性名】
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            // 封装属性ID
            SearchResult.AttrVo attr = new SearchResult.AttrVo();
            attr.setAttrId(bucket.getKeyAsNumber().longValue());
            // 封装属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            attr.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            // 封装属性值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValue = attrValueAgg.getBuckets().stream()
                    .map(valueBucket -> valueBucket.getKeyAsString())
                    .collect(Collectors.toList());
            attr.setAttrValue(attrValue);
            attrs.add(attr);
            // 构建面包屑数据源
            if (!CollectionUtils.isEmpty(param.getAttrs()) && !attrMap.containsKey(attr.getAttrId())) {
                attrMap.put(attr.getAttrId(), attr.getAttrName());
            }
        }
        result.setAttrs(attrs);

        // 4.当前所有商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brands = new ArrayList<>();
        ParsedLongTerms brandAgg = aggregations.get("brand_agg");
        Map<Long, String> brandMap = new HashMap<>();// 面包屑map数据源【品牌】
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            // 封装品牌ID
            SearchResult.BrandVo brand = new SearchResult.BrandVo();
            brand.setBrandId(bucket.getKeyAsNumber().longValue());
            // 封装品牌名
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            brand.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());
            // 封装品牌图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            brand.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            brands.add(brand);
            // 构建面包屑数据源
            if (!CollectionUtils.isEmpty(param.getBrandId()) ) {
                brandMap.put(brand.getBrandId(), brand.getBrandName());
            }
        }
        result.setBrands(brands);

        // 5.当前所有商品涉及到的所有分类信息
        List<SearchResult.CatalogVo> catalogs = new ArrayList<>();
        ParsedLongTerms catalogAgg = aggregations.get("catalog_agg");
        String catalogName = null;// 面包屑map数据源【分类】
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            // 封装分类ID
            SearchResult.CatalogVo catalog = new SearchResult.CatalogVo();
            catalog.setCatalogId(bucket.getKeyAsNumber().longValue());
            // 封装分类名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");// 子聚合
            catalog.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());
            catalogs.add(catalog);
            // 构建面包屑数据源
            if (catalog.getCatalogId().equals(param.getCatalog3Id())) {
                catalogName = catalog.getCatalogName();
            }
        }
        result.setCatalogs(catalogs);

        // 6.构建面包屑导航数据_属性
        if (!CollectionUtils.isEmpty(param.getAttrs())) {
            // 属性非空才需要面包屑功能
            List<SearchResult.NavVo> navs = param.getAttrs().stream().map(attr -> {
                // attr：15_海思
                SearchResult.NavVo nav = new SearchResult.NavVo();
                String[] arr = attr.split("_");
                // 封装筛选属性ID集合【给前端判断哪些属性是筛选条件，从而隐藏显示属性栏，显示在面包屑中】
                result.getAttrIds().add(Long.parseLong(arr[0]));
                // 面包屑名字：属性名
                nav.setNavName(attrMap.get(Long.parseLong(arr[0])));
                // 面包屑值：属性值
                nav.setNavValue(arr[1]);
                // 设置跳转地址（将属性条件置空）【当取消面包屑上的条件时，跳转地址】
                String replace = replaceQueryString(param, "attrs", attr);
                nav.setLink("http://search.gulimall.com/list.html?" + replace);// 每一个属性都有自己对应的回退地址

                return nav;
            }).collect(Collectors.toList());
            result.setNavs(navs);
        }

        // 7.构建面包屑导航数据_品牌
        if (!CollectionUtils.isEmpty(param.getBrandId())) {
            List<SearchResult.NavVo> navs = result.getNavs();
            // 多个品牌ID封装成一级面包屑，所以这里只需要一个NavVo
            SearchResult.NavVo nav = new SearchResult.NavVo();
            // 面包屑名称直接使用品牌
            nav.setNavName("品牌");
            StringBuffer buffer = new StringBuffer();
            String replace = "";
            for (Long brandId : param.getBrandId()) {
                // 多个brandId筛选条件汇总为一级面包屑，所以navValue拼接所有品牌名
                buffer.append(brandMap.get(brandId)).append(";");
                // 因为多个brandId汇总为一级面包屑，所以每一个brandId筛选条件都要删除
                replace = replaceQueryString(param, "brandId", brandId.toString());
            }
            nav.setNavValue(buffer.toString());// 品牌拼接值
            nav.setLink("http://search.gulimall.com/list.html?" + replace);// 回退品牌面包屑等于删除所有品牌条件
            navs.add(nav);
        }

        // 构建面包屑导航数据_分类
        if (param.getCatalog3Id() != null) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo nav = new SearchResult.NavVo();
            nav.setNavName("分类");
            nav.setNavValue(catalogName);// 分类名
            StringBuffer buffer = new StringBuffer();
//            String replace = replaceQueryString(param, "catalog3Id", param.getCatalog3Id().toString());
//            nav.setLink("http://search.gulimall.com/list.html?" + replace);
            navs.add(nav);
        }


        return result;
    }

    private String replaceQueryString(SearchParam param, String key, String value) {
        // 解决编码问题，前端参数使用UTF-8编码了
        String encode = null;
        encode = UriEncoder.encode(value);
//                try {
//                    encode = URLEncoder.encode(attr, "UTF-8");// java将空格转义成了+号
//                    encode = encode.replace("+", "%20");// 浏览器将空格转义成了%20，差异化处理，否则_queryString与encode匹配失败
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
        // TODO BUG，第一个参数不带&
        // 替换掉当前查询条件，剩下的查询条件即是回退地址
        String replace = param.get_queryString().replace("&" + key + "=" + encode, "");
        return replace;
    }


}