package com.atguigu.gmall.search.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wyl
 * @create 2020-06-28 15:05
 */
@ConfigurationProperties("gmall.message.search")
@Data
public class SearchMessageProperties {
    private String exchangeName;
    private String queueName;
    private String searchTopicPattern;
}
