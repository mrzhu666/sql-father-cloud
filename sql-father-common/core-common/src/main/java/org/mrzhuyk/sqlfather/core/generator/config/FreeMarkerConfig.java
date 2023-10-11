package org.mrzhuyk.sqlfather.core.generator.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * 通过单例方式导入配置类
 */
@Slf4j
public class FreeMarkerConfig {
    
    private static Configuration cfg;
    
    @SneakyThrows
    public static Configuration getCfg() {
        if (cfg == null) {
            synchronized (FreeMarkerConfig.class) {
                if (cfg == null) {
                    cfg = new Configuration(Configuration.VERSION_2_3_29);
                    cfg.setClassForTemplateLoading(FreeMarkerConfig.class,"/templates");
                    cfg.setDefaultEncoding("UTF-8");
                    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    cfg.setLogTemplateExceptions(false);
                    cfg.setWrapUncheckedExceptions(true);
                    cfg.setFallbackOnNullLoopVariable(false);
                }
            }
        }
        return cfg;
    }
}
