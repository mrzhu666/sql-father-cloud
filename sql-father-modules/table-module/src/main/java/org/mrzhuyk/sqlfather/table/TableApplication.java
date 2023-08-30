package org.mrzhuyk.sqlfather.table;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@SpringBootApplication
public class TableApplication {
    public static void main(String[] args) {
        SpringApplication.run(TableApplication.class, args);
    }
}
