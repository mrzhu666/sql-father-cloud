package org.mrzhuyk.sqlfather.user;


import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCustomConfig
@EnableCustomFeign
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
