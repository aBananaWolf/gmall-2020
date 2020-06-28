package com.atguigu.gmall.search.mq;

import com.atguigu.gmall.search.service.ImportDataService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author wyl
 * @create 2020-06-28 16:51
 */
@Component
public class SearchDataListener {
    @Autowired
    private ImportDataService importDataService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${gmall.message.search.queue-name}",durable = "true", ignoreDeclarationExceptions = "true"),
            exchange = @Exchange(value = "${gmall.message.search.exchange-name}",type = "topic" ,ignoreDeclarationExceptions = "true", durable = "true"),
            key = "${gmall.message.search.search-topic-pattern}"

    ))
    public void saveSearchData(Long spuId , Channel channel, Message message) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            channel.basicAck(deliveryTag,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        importDataService.importDataBySpuId(spuId);
    }

}
