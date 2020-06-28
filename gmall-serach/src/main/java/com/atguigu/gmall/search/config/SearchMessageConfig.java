package com.atguigu.gmall.search.config;

import com.atguigu.gmall.search.properties.SearchMessageProperties;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wyl
 * @create 2020-06-28 17:26
 */
@Configuration
@EnableConfigurationProperties(SearchMessageProperties.class)
public class SearchMessageConfig {

    @Bean
    public MessageConverter messageConverter () {
        return new Jackson2JsonMessageConverter();
    }
}
