package org.mrzhuyk.sqlfather.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 多个角色中的任意一个角色
     *
     * @return
     */
    String[] anyRole() default "";
    
    /**
     * 必须有某个角色
     *
     * @return
     */
    String mustRole() default "";
    
}
