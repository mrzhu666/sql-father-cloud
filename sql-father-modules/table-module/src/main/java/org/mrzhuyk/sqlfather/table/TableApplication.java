package org.mrzhuyk.sqlfather.table;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableCustomFeign
@SpringBootApplication
public class TableApplication {
    public static void main(String[] args) {
        SpringApplication.run(TableApplication.class, args);
    }
}
