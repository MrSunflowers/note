package com.atguigu.gulimall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallSearchApplicationTest {

    @Autowired
    private RestHighLevelClient client;

    /**
     * 保存/更新索引
     */
//    @Test
//    void indexData() throws IOException {
//        // 1、构建创建或更新请求，指定索引users
//        IndexRequest indexRequest = new IndexRequest( "users");
//        // 2、设置id
//        indexRequest.id("1");// 数据的id
//        // 方式一：直接设置数据项
//        //users.source("userName", "zhangsan", "gender", "M", "age", "18");
//        User user = new User("lisi", "M", 22);
//        // 3、绑定数据与请求
//        // 方式二：设置json串格式数据
//        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
//        // 4、执行：同步
//        IndexResponse index = client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
//        // 5、提取响应数据
//        System.out.println(index);
//    }
//
//    @Data
//    class User{
//        private String userName;
//        private String gender;
//        private Integer age;
//
//        public User(String userName, String gender, Integer age) {
//            this.userName = userName;
//            this.gender = gender;
//            this.age = age;
//        }
//    }

    /**
     * 从es中查询数据
     * 1、创建检索请求
     *      SearchRequest searchRequest = new SearchRequest("newbank")
     * 2、创建检索条件构建对象（SearchSourceBuilder用于构建检索条件的builder对象）
     *      SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
     *      绑定操作：
     *         sourceBuilder.sort("age");
     *         sourceBuilder.from(1);
     *         sourceBuilder.size(10);
     *         sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age").size(10));
     *         sourceBuilder.aggregation(AggregationBuilders.avg("balanceAvg").field("balance"));
     *         sourceBuilder.query(QueryBuilders.matchAllQuery("address", "mill"));
     *         sourceBuilder.query(QueryBuilders.boolQuery());
     * 3、请求绑定条件
     *      searchRequest.source(sourceBuilder);
     * 4、执行请求，接收结果
     * SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
     * 5、获取命中结果
     *      SearchHit[] hits = searchResponse.getHits().getHits();
     *      Account account = JSON.parseObject(hit.getSourceAsString(), Account.class);
     * 6、获取分组结果
     *      Aggregations aggregations = searchResponse.getAggregations();
     *      年龄分布
     *      Terms ageAgg = aggregations.get("ageAgg");
     *      for (Terms.Bucket bucket : ageAgg.getBuckets()) {
     *          System.out.println("年龄：" + bucket.getKeyAsString() + "--人数： " + bucket.getDocCount());
     *      }
     *      平均工资
     *      Avg balanceAvg = aggregations.get("balanceAvg");
     *      double balance = balanceAvg.getValue();
     */
//    @Test
//    void searchData() throws IOException {
//        // 1、创建检索请求，自定索引（调用该方法可以切换索引searchRequest.indices("bank")）
//        SearchRequest searchRequest = new SearchRequest("newbank");
//        // 2、构建检索条件，DSL
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        /**
//         * GET bank/_search
//         * {
//         *   "query": {
//         *     "bool": {
//         *       "must": [
//         *         {
//         *           "match": {
//         *             "address": "mill"
//         *           }
//         *         }
//         *       ]
//         *     }
//         *   },
//         *   "aggs": {
//         *     "ageAgg": {
//         *       "terms": {
//         *         "field": "age",
//         *         "size": 10
//         *       }
//         *     },
//         *     "blanceAvg":{
//         *       "avg": {
//         *         "field": "balance"
//         *       }
//         *     }
//         *   }
//         * }
//         * 需求：
//         *  搜索条件：1、检索address中包含mill的所有人
//         *  分组条件：1、年龄分布
//         *           2、平均薪资
//         */
//        // 模糊匹配address=mill
//        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
//        // 年龄分布，取前10条
//        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("ageAgg").field("age").size(10);
//        sourceBuilder.aggregation(termsAggregationBuilder);
//        // 平均薪资
//        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balanceAvg").field("balance");
//        sourceBuilder.aggregation(avgAggregationBuilder);
//        System.out.println("检索条件:" + sourceBuilder);
//        // 3、检索请求绑定条件
//        searchRequest.source(sourceBuilder);
//        // 4、执行请求，接收结果
//        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
//        // 5、获取命中的数据【Map map = JSON.parseObject(searchResponse.toString(), Map.class)】
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        for (SearchHit hit : hits) {
//            // "hits" : [
//            //      {
//            //        "_index" : "newbank",
//            //        "_type" : "_doc",
//            //        "_id" : "10",
//            //        "_score" : null,
//            //        "_source" : {
//            //          "account_number" : 10,
//            //          "balance" : 46170,
//            //          "firstname" : "Dominique",
//            //          "lastname" : "Park",
//            //          "age" : 37,
//            //          "gender" : "F",
//            //          "address" : "100 Gatling Place",
//            //          "employer" : "Conjurica",
//            //          "email" : "dominiquepark@conjurica.com",
//            //          "city" : "Omar",
//            //          "state" : "NJ"
//            //        },
//            //        "sort" : [
//            //          10
//            //        ]
//            //      }
////            hit.getIndex();hit.getType();hit.getId();
//            // 将_source封装Object对象
//            Account account = JSON.parseObject(hit.getSourceAsString(), Account.class);
//            System.out.println("搜索结果： " + account);
//        }
//        // 6.获得分组结果
//        Aggregations aggregations = searchResponse.getAggregations();
//        // 通过名字获得年龄分布
//        Terms ageAgg = aggregations.get("ageAgg");
//        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
//            System.out.println("年龄：" + bucket.getKeyAsString() + "--人数： " + bucket.getDocCount());
//        }
//        // 获取平均薪资
//        Avg balanceAvg = aggregations.get("balanceAvg");
//        System.out.println("薪资平均值： " + balanceAvg.getValue());
//    }
//
//    @Data
//    static class Account {
//        private int account_number;
//        private int balance;
//        private String firstname;
//        private String lastname;
//        private int age;
//        private String gender;
//        private String address;
//        private String employer;
//        private String email;
//        private String city;
//        private String state;
//
//        @Override
//        public String toString() {
//            return "Account{" +
//                    "account_number=" + account_number +
//                    ", balance=" + balance +
//                    ", firstname='" + firstname + '\'' +
//                    ", lastname='" + lastname + '\'' +
//                    ", age=" + age +
//                    ", gender='" + gender + '\'' +
//                    ", address='" + address + '\'' +
//                    ", employer='" + employer + '\'' +
//                    ", email='" + email + '\'' +
//                    ", city='" + city + '\'' +
//                    ", state='" + state + '\'' +
//                    '}';
//        }
//    }
}
