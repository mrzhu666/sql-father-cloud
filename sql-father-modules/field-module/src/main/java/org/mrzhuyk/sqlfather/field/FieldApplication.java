package org.mrzhuyk.sqlfather.field;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@SpringBootApplication
@EnableCustomFeign
public class FieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(FieldApplication.class,args);
    }
}
