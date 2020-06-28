package com.atguigu.gmall.search.config;

import com.atguigu.gmall.search.properties.SearchProperties;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * @author wyl
 * @create 2020-06-22 16:28
 */
@Configuration
@EnableConfigurationProperties(SearchProperties.class)
public class SearchConfig {
    @Autowired
    private SearchProperties searchProperties;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        if(searchProperties.getAddress() == null)
            return null;
        HttpHost[] httpHosts = new HttpHost[searchProperties.getAddress().length];

        for (int i = 0; i < searchProperties.getAddress().length; i++) {
            String[] hostAndPort = searchProperties.getAddress()[i].split("\\:");
            httpHosts[i] = new HttpHost(hostAndPort[0],Integer.parseInt(hostAndPort[1]));
        }

        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }
}
