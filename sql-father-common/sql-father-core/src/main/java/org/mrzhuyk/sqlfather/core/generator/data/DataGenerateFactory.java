package org.mrzhuyk.sqlfather.core.generator.data;

import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.List;

/**
 * 数据工厂
 * 工厂+单例
 */
public class DataGenerateFactory implements DataGenerate {
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        return null;
    }
}
