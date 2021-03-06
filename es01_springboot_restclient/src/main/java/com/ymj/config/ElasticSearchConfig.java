package com.ymj.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname ElasticSearchConfig
 * @Description TODO
 * @Date 2021/9/8 15:37
 * @Created by yemingjie
 */
@Configuration
public class ElasticSearchConfig {
    @Value("${lagouelasticsearch.elasticsearch.hostlist}")
    private String hostlist;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 解析hostlist信息
        String[] split = hostlist.split(",");

        HttpHost[] httpHosts = new HttpHost[split.length];
        for (int i = 0; i < split.length; i++) {
            String item = split[i];
            httpHosts[i] = new HttpHost(item.split(":")[0],
                    Integer.parseInt(item.split(":")[1]), "http");
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    //项目主要使用RestHighLevelClient，对于低级的客户端暂时不用
    @Bean
    public RestClient restClient(){
        //解析hostlist配置信息
        String[] split = hostlist.split(",");
        //创建HttpHost数组，其中存放es主机和端口的配置信息
        HttpHost[] httpHostArray = new HttpHost[split.length];
        for(int i=0;i<split.length;i++){
            String item = split[i];
            httpHostArray[i] = new HttpHost(item.split(":")[0],
                    Integer.parseInt(item.split(":")[1]), "http");
        }
        return RestClient.builder(httpHostArray).build();
    }
}
