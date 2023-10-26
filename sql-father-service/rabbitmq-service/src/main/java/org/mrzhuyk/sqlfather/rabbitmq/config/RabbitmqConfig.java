package org.mrzhuyk.sqlfather.rabbitmq.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {
    public static final String QUEUE_REDIS ="redis";
    
    public static final String TOPIC_EXCHANGE_NAME ="redis";
    
    public static final String ROUTING_KEY = "redis.dict";
    
    @Bean
    public Queue queue() {
        return QueueBuilder
            .nonDurable(QUEUE_REDIS)
            .build();
    }
    
    @Bean
    public TopicExchange exchange() {
        return ExchangeBuilder
            .topicExchange(TOPIC_EXCHANGE_NAME)
            .durable(false)
            .build();
    }
    
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
            .bind(queue)
            .to(exchange)
            .with(ROUTING_KEY);
    }
}
