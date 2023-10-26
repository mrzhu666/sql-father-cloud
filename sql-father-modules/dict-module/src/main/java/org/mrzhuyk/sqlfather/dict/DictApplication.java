package org.mrzhuyk.sqlfather.dict;


import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.mrzhuyk.sqlfather.rabbitmq.RabbitmqService;
import org.mrzhuyk.sqlfather.rabbitmq.config.RabbitmqConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@Slf4j
@SpringBootApplication
@EnableCustomConfig
@EnableCustomFeign
public class DictApplication implements CommandLineRunner {
    @Resource
    RabbitmqService rabbitmqService;
    
    public static void main(String[] args) {
        SpringApplication.run(DictApplication.class, args);
    }
    
    /**
     * 判断resources下的文件是否存在
     * @param args incoming main method arguments
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        String message="key";
        rabbitmqService.send(message, RabbitmqConfig.TOPIC_EXCHANGE_NAME,RabbitmqConfig.ROUTING_KEY);
    }
}
