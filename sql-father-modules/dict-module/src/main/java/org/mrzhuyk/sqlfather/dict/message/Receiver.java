package org.mrzhuyk.sqlfather.dict.message;


import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Receiver {
    private final static Gson GSON = new Gson();
    
    
    @Resource
    RedisTemplate<String, Object> redisTemplate;
    
    @RabbitListener(queues = RabbitmqConfig.QUEUE_REDIS)
    public void receive(String messageStr, Channel channel, Message message) throws IOException {
        log.info("接收消息");
        // 获取消息id，存储到redis，防止重复消费
        String messageID = (String) message.getMessageProperties().getHeaders().get("spring_returned_message_correlation");
        try {
            // 保存消息id到redis，缓存时间为10秒
            if (BooleanUtils.isTrue(redisTemplate.opsForValue().setIfAbsent(messageID, "0", 10, TimeUnit.SECONDS))) {
                
                //消费消息
                String msg = GSON.fromJson(messageStr, String.class);
                //System.out.println(msg);
                redisClear(msg);
                
                //将value设置为1
                redisTemplate.opsForValue().set(messageID, "1", 10, TimeUnit.SECONDS);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                // 拒绝消息，且不返回消息到队列，将变为死信。如果消费失败需要手动删除该键？
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                // 判断是否消费过。如果判断出0，说明被其它消费者消费？会不会和消费失败冲突？
                if (StringUtils.equalsIgnoreCase("1", (String)redisTemplate.opsForValue().get(messageID))) {
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息
                }
            }
            
        } catch (Exception e) {
            // 发生异常，说明消费失败
            // 删除消息ID
            redisTemplate.delete(messageID);
            // 拒绝确认消息
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            System.err.println("get msg1 failed msg = " + messageStr);
        }
    }
    
    /**
     * 根据前缀批量删除缓存
     * @param prefix
     */
    public void redisClear(String prefix) {
        if(!prefix.endsWith(":")) prefix += ":";
        
        ScanOptions options = ScanOptions.scanOptions().match(prefix+"*").count(1000)
            .build();
        Cursor<String> cursor = (Cursor<String>) redisTemplate.executeWithStickyConnection(
            redisConnection -> new ConvertingCursor<>(redisConnection.scan(options),
                redisTemplate.getKeySerializer()::deserialize));
        if(cursor==null) return;
        cursor.forEachRemaining(key -> {
            redisTemplate.delete(key);
        });
    }
}
