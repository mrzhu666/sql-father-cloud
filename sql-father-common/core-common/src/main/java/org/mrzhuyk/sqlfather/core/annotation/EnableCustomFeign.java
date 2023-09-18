package org.mrzhuyk.sqlfather.core.annotation;


import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.*;

/**
 * 自定义openfeign注解
 *  指定basePackages扫描路径，不指定的话接口类需要在启动类所在包之内
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableFeignClients
public @interface EnableCustomFeign {
    String[] value() default {};
    
    String[] basePackages() default { "org.mrzhuyk.sqlfather" };
    
    Class<?>[] basePackageClasses() default {};
    
    Class<?>[] defaultConfiguration() default {};
    
    Class<?>[] clients() default {};
}

