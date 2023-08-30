package org.mrzhuyk.sqlfather.sql;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
    @Value("${spring.application.name}")
    //@Value("${spring.bootstrap.enable}")
    private String property;
    
    @Test
    void contextLoads() {
        System.out.println(property);
    }
}
