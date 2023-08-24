package org.mrzhuyk.sqlfather.user;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCustomConfig
public class SqlFatherUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlFatherUserApplication.class, args);
    }
}
