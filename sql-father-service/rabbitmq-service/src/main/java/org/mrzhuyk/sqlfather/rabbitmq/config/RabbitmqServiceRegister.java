package org.mrzhuyk.sqlfather.rabbitmq.config;

import org.mrzhuyk.sqlfather.rabbitmq.RabbitmqService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqServiceRegister {
    @Bean
    public RabbitmqService rabbitmqService() {
        return new RabbitmqService();
    }
}
