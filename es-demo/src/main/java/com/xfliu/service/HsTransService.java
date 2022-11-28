package com.xfliu.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xfliu.dao.mapper.CardTransMapper;
import com.xfliu.dao.model.CardTrans;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class HsTransService {

    @Resource
    RestHighLevelClient restHighLevelClient;


    @Resource
    private CardTransMapper cardTransMapper;


    public void createIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hs_trans1");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
            builder.startObject("properties");
                builder.startObject("cardNo");
                builder.field("type", "text")
                        .startObject("fields")
                        .startObject("keyword").field("type", "keyword")
                        .endObject()
                        .endObject();
                builder.endObject();

                builder.startObject("pid");
                builder.field("type", "text");
                builder.endObject();

                builder.startObject("cardStatus");
                builder.field("type", "integer");
                builder.endObject();

                builder.startObject("resultTime");
                builder.field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                        .startObject("fields")
                        .startObject("keyword").field("type", "keyword")
                        .endObject()
                        .endObject();
                builder.endObject();

                builder.startObject("updateTimeTrans");
                builder.field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                        .startObject("fields")
                        .startObject("keyword").field("type", "keyword")
                        .endObject()
                        .endObject();
                builder.endObject();

                builder.startObject("updateTime");
                builder.field("type","date")
                        .field("format", "yyyy-MM-dd HH:mm:ss")
                        .startObject("fields")
                        .startObject("keyword").field("type", "keyword")
                        .endObject()
                        .endObject();
                builder.endObject();

            builder.endObject();
        builder.endObject();
        request.mapping(builder);

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        // 判断是否创建成功
        boolean isCreated = createIndexResponse.isAcknowledged();
        log.info("是否创建成功：{}", isCreated);

    }



    public void indexEs(CardTrans cardTrans) throws Exception {
        IndexRequest indexRequest = new IndexRequest("hs_trans");

        String source = JSON.toJSONString(cardTrans);

        indexRequest.source(source, XContentType.JSON);

        // 操作ES
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

    }

    public void indexDelete(String index) throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest(index);

        // 操作ES
        restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    }


    @Async
    public void dealFile( File f)throws Exception{
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);

        String line = "";
        int i=0;
        int size = 20000;
        List<CardTrans> retryList = new ArrayList<>();

        while ((line = reader.readLine()) != null) {

            retryList.add(JSONObject.parseObject(line,CardTrans.class));
            if (retryList.size()==size){
                bulkIndex(retryList);
                log.info(""+i++);
                log.info(f.getName());
                retryList.clear();
            }
        }if (CollectionUtil.isNotEmpty(retryList)){
            bulkIndex(retryList);
        }
    }


    public void bulkIndex(List<CardTrans> list) throws IOException {

        BulkRequest request = new BulkRequest();

        list.stream().forEach(s -> {

            String source = JSON.toJSONString(s);
            request.add(new IndexRequest("hs_trans1").source(source, XContentType.JSON));
        });
        restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

    }


}
