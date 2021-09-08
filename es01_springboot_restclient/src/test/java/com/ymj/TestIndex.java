package com.ymj;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Classname TestIndex
 * @Description TODO
 * @Date 2021/9/8 15:45
 * @Created by yemingjie
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {
    @Autowired
    private RestHighLevelClient client;


    /**
     * 创建索引库
     * PUT /elasticsearch_test
     * {
     *   "settings": {},
     *   "mappings": {
     *     "properties": {
     *       "description": {
     *         "type": "text",
     *         "analyzer": "ik_max_word"
     *       },
     *       "name": {
     *         "type": "keyword"
     *       },
     *       "pic": {
     *         "type": "text",
     *         "index": false
     *       },
     *       "studymodel": {
     *         "type": "keyword"
     *       }
     *     }
     *   }
     * }
     */
    @Test
    public void testCreateIndex() throws Exception {
        // 创建索引对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("elasticsearch_test");

        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", "1")
                .put("number_of_replicas", "0"));
        // 指定映射
        // ===========================第一种==============================
//        XContentBuilder builder = XContentFactory.jsonBuilder()
//                .startObject()
//                .field("properties")
//                .startObject()
//                .field("studymodel").startObject().field("index",
//                        "true").field("type", "keyword").endObject()
//                .field("description").startObject().field("index",
//                        "true").field("type", "text").field("analyzer", "ik_max_word").endObject()
//                .field("name").startObject().field("index",
//                        "true").field("type", "integer").endObject()
//                .field("pic").startObject().field("index",
//                        "false").field("type", "text").endObject()
//                .endObject()
//                .endObject();
//        createIndexRequest.mapping(builder);

        // ===========================第二种==============================
        createIndexRequest.mapping(" {\n" +
                "\t\"properties\": {\n" +
                "      \"description\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_max_word\"\n" +
                "      },\n" +
                "      \"name\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"pic\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"index\": false\n" +
                "      },\n" +
                "      \"studymodel\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      }\n" +
                "   \t}\n" +
                "}", XContentType.JSON);

        // =========================================================
        // 操作索引的客户端
        IndicesClient indicesClient = client.indices();
        CreateIndexResponse createIndexResponse = indicesClient.create(createIndexRequest, RequestOptions.DEFAULT);
        // 得到响应
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println("======================" + acknowledged + "======================");
    }


    /**
     * 删除索引库
     * @throws IOException
     */
    @Test
    public void testDeleteIndex() throws IOException {
        // 删除索引的请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("elasticsearch_test");
        // 操作索引的客户端
        IndicesClient indices = client.indices();
        // 执行删除索引
        AcknowledgedResponse delete = indices.delete(deleteIndexRequest, RequestOptions.DEFAULT);
        // 得到响应
        boolean acknowledged = delete.isAcknowledged();
        System.out.println("======================" + acknowledged + "======================");
    }
}











