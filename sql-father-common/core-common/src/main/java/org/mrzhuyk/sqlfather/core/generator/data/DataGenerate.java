package org.mrzhuyk.sqlfather.core.generator.data;

import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.List;

/**
 * 数据模拟生成
 */
public interface DataGenerate {
    /**
     * 生成数据
     *
     * @param field 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(TableSchema.Field field, int rowNum);
    
}
