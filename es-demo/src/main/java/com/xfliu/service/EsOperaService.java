package com.xfliu.service;

import com.alibaba.fastjson.JSON;
import com.xfliu.entity.User;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EsOperaService {

    @Resource
    RestHighLevelClient restHighLevelClient;

    public void add ()throws Exception{
        IndexRequest indexRequest = new IndexRequest("users");
        User user = new User();
        user.setUserName("张三");
        user.setAge(20);
        user.setGender("男");
        String source = JSON.toJSONString(user);
        indexRequest.id("1").source(source, XContentType.JSON);
        // 操作ES
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

    public void update()throws Exception{
        UpdateRequest updateRequest = new UpdateRequest("users", "1");
        User user = new User();
        user.setUserName("李四");
        user.setAge(20);
        user.setGender("女");
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        // 操作ES
        restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
    }

    public void delete() throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest("users", "1");
        // 操作ES
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);

    }

    public void serch() throws Exception {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("users");
        // 构建检索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 分页采用简单的from + size分页，适用数据量小的，了解更多分页方式可自行查阅资料
        //        searchSourceBuilder.from((page - 1) * rows);
        //        searchSourceBuilder.size(rows);
        // 查询所有
        //        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        // 根据字段匹配
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("userName","张三");
        searchSourceBuilder.query(queryBuilder);

        searchRequest.source(searchSourceBuilder);
        // 查询ES
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("查询结果：" + searchResponse.toString());
        SearchHits hits = searchResponse.getHits();
        // 遍历封装列表对象
        List<User> userList = new ArrayList<>();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            userList.add(JSON.parseObject(searchHit.getSourceAsString(), User.class));
        }
        System.out.println(userList);

    }

    public void find () throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //AggregationBuilders工具类构建AggregationBuilder
        // 构建第一个聚合条件:按照年龄的值分布
        TermsAggregationBuilder agg1 = AggregationBuilders.terms("agg1").field("age").size(10);// 聚合名称
        // 参数为AggregationBuilder
        sourceBuilder.aggregation(agg1);
        // 构建第二个聚合条件:平均薪资
        AvgAggregationBuilder agg2 = AggregationBuilders.avg("agg2").field("balance");
        sourceBuilder.aggregation(agg2);

        System.out.println("检索条件"+sourceBuilder.toString());

        searchRequest.source(sourceBuilder);

        // 2 执行检索
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 3 分析响应结果
        System.out.println(response.toString());
    }
}
