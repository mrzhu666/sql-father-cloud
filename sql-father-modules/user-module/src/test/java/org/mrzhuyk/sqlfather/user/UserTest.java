package org.mrzhuyk.sqlfather.user;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class UserTest {
    //@Value("${spring.application.name}")
    ////@Value("${spring.bootstrap.enable}")
    //private String property;
    
    @Test
    void contextLoads() {
        System.out.println(StringUtils.joinWith(":","a","b"));
    }
}
