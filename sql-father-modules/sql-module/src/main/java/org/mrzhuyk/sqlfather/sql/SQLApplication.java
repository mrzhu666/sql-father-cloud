package org.mrzhuyk.sqlfather.sql;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableCustomConfig
@EnableCustomFeign
public class SQLApplication {
    public static void main(String[] args) {
        SpringApplication.run(SQLApplication.class, args);
    }
}
