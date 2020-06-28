package com.atguigu.gmall.search.properties;

import org.apache.http.HttpHost;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wyl
 * @create 2020-06-22 16:40
 */
@ConfigurationProperties("search.config")
public class SearchProperties {
    private String[] address;

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }
}
