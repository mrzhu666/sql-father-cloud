package org.mrzhuyk.sqlfather.dict;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomConfig;
import org.mrzhuyk.sqlfather.core.annotation.EnableCustomFeign;
import org.mrzhuyk.sqlfather.core.generator.config.FreeMarkerConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.URL;

@Slf4j
@SpringBootApplication
@EnableCustomConfig
@EnableCustomFeign
public class DictApplication implements CommandLineRunner {
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
        //URL templates = DictApplication.class.getResource("/templates");  //一定要加斜杆，不然返回空。本地运行可以，jar包不行文件不存在。
        URL templates = DictApplication.class.getClassLoader().getResource("templates"); //本地运行可以，jar包不行文件不存在
        if (templates == null) {
            log.info("templates is null");
            return;
        } else {
            log.info("templates exists");
        }
        File file = new File(templates.getFile());
        log.info("templates folder exists is "+file.exists());
    }
}
