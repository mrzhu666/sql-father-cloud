package org.mrzhuyk.sqlfather.user;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.mrzhuyk.sqlfather.user.mapper")
public class SqlFatherUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(SqlFatherUserApplication.class, args);
    }
}
