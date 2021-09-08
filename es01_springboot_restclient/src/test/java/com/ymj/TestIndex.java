package com.ymj;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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


    /**
     * 添加文档
     * POST /elasticsearch_test/_doc/1
     * {
     *   "name": "spring cloud实战",
     *   "description": "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基 础入门 3.实战Spring Boot 4.注册中心eureka。",
     *   "studymodel": "201001",
     *   "timestamp": "2020-08-22 20:09:18",
     *   "price": 5.6
     * }
     * @throws IOException
     */
    @Test
    public void testAddDoc() throws IOException {
        //创建索引请求对象
        IndexRequest indexRequest = new IndexRequest("elasticsearch_test", "doc");
        indexRequest.id("1");
        //文档内容 准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        indexRequest.source(jsonMap);
        //通过client进行http的请求
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println("======================" + result + "======================");
    }


    /**
     * 查询文档
     * @throws IOException
     */
    @Test
    public void testGetDoc() throws IOException {
        // 查询请求对象
        GetRequest getRequest = new GetRequest("elasticsearch_test", "2");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        // 得到文档的内容
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println(sourceAsMap);
    }


    /**
     * 搜索全部记录
     * GET /elasticsearch_test/_search
     * {
     *   "query": {
     *     "match_all": {}
     *   }
     * }
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testSearchAll() throws IOException, ParseException {
        // 搜索请求对象
        SearchRequest searchRequest = new SearchRequest("elasticsearch_test");
        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索方式
        // matchAllQuery搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        // 设置源字段过滤,第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[] {"name","studymodel","price","timestamp"}, new String[]{});
        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);
        // 执行搜索,向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();
        // 匹配到的总记录数
        TotalHits totalHits = hits.getTotalHits();
        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit hit : searchHits) {
            // 文档的主键
            String id = hit.getId();
            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            // 由于前边设置了源文档字段过滤，这时description是取不到的
            String description = (String) sourceAsMap.get("description");
            // 学习模式
            String studymodel = (String) sourceAsMap.get("studymodel");
            // 价格
            Double price = (Double) sourceAsMap.get("price");
            // 日期
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println("======================" + name + "======================");
            System.out.println("======================" + studymodel + "======================");
            System.out.println("======================" + description + "======================");
        }


    }
}











