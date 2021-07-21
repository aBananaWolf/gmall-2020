package com.atguigu.gmall.pms.config;

import com.atguigu.gmall.pms.properties.SearchMessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author wyl
 * @create 2020-06-28 15:27
 */
@Component
@Slf4j
public class BasicMessageSender {
    @Autowired
    private SearchMessageProperties messageProperties;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private String SEND_MESSAGE_COUNT = "SendMessageCount";
    @Value("${spring.rabbitmq.template.retry.max-attempts}")
    private Integer SEND_MESSAGE_MAX = 3;

    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {

        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            log.error(routingKey + " -> " + exchange + " -> " + replyText);
        }
    };

    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if (!ack) {
                if (correlationData != null) {
                    Integer sendCount = correlationData.getReturnedMessage().getMessageProperties().getHeader("sendCount");
                    if (sendCount > SEND_MESSAGE_MAX) {
                        log.error("out of retries for send message");
                        // 发送邮件(非常重要，视这里为重试次数用尽)
                    }
                }
            }
        }
    };


    public void sendMessage(String message, String pattern) {
        // 失效路由
        rabbitTemplate.setReturnCallback(returnCallback);
        // 消息监听
        rabbitTemplate.setConfirmCallback(confirmCallback);

        Message build = MessageBuilder.withBody(message.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .setHeader(SEND_MESSAGE_COUNT,1)
                .build();
        // 发送消息
        rabbitTemplate.send(messageProperties.getExchangeName(),
                pattern,build);
    }
}
