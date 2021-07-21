package com.atguigu.gmall.pms.config;

import com.atguigu.gmall.pms.properties.SearchMessageProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wyl
 * @create 2020-06-28 15:29
 */
@Configuration
@EnableConfigurationProperties(SearchMessageProperties.class)
public class BasicMQConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange exchange() {
        Exchange exchange = new ExchangeBuilder("aaaa", "topic").build();
        return exchange;
    }
    @Bean
    public Queue queue() {
        Queue queue = new Queue("bbbb", true, false ,false);
        return queue;
    }

    @Bean
    public Binding binding(Exchange exchange, Queue queue) {
        Binding cccc = BindingBuilder.bind(queue).to(exchange).with("cccc").noargs();
        return cccc;
    }
}
