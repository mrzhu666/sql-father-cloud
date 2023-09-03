package org.mrzhuyk.sqlfather.core.generator.config;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;

import java.io.File;
import java.io.IOException;

/**
 * 通过静态方法导入配置类
 */
public class FreeMarkerConfig {
    
    private final static Configuration cfg = new Configuration(Configuration.VERSION_2_3_29){{
        try {
            setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
            setDefaultEncoding("UTF-8");
            setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            setLogTemplateExceptions(false);
            setWrapUncheckedExceptions(true);
            setFallbackOnNullLoopVariable(false);
        } catch (IOException e) {
            throw new BizException(ErrorEnum.INTERNAL_SERVER_ERROR,"内部FreeMarker错误");
        }
    }};
    
    public static Configuration getCfg() {
        return cfg;
    }
}
