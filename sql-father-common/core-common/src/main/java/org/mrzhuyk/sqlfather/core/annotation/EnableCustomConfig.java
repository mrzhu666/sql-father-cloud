package org.mrzhuyk.sqlfather.core.annotation;


import org.mybatis.spring.annotation.MapperScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
// 指定要扫描的Mapper类的包的路径
@MapperScan("org.mrzhuyk.sqlfather.**.mapper")
// 自定义feign启动

public @interface EnableCustomConfig {
}
