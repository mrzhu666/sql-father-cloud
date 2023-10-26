package org.mrzhuyk.sqlfather.rabbitmq;

import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.channels.Channel;
import java.util.UUID;

@Component
public class RabbitmqService {
    private final static Gson GSON = new Gson();
    
    @Resource
    RabbitTemplate rabbitTemplate;
    
    public void send(Object message, String exchangeName,String routingKey) {
        String json=GSON.toJson(message);
        CorrelationData messageID = new CorrelationData(UUID.randomUUID().toString());  // 生成消息id
        rabbitTemplate.convertAndSend(exchangeName, routingKey, json,messageID);
    }
}
