package org.mrzhuyk.sqlfather.core.generator.builder;


import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.generator.dialect.SQLDialect;

/**
 * SQL生成器
 * 支持方言，策略模式
 */
@Slf4j
public class SQLBuilder {
    private SQLDialect sqlDialect;
}
