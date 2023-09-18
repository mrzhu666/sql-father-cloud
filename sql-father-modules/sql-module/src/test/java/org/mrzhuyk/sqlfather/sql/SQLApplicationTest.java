package org.mrzhuyk.sqlfather.sql;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;

class SQLApplicationTest {
    @Test
    @SneakyThrows
    public void testFile() {
        //File file = new File("src/main/resources/templates/java_entity.ftl");
        File file = new File("src/main/resources/templates");
        System.out.println(file.exists());
        
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        
    }
}