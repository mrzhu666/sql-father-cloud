package org.mrzhuyk.sqlfather.core.generator.dialect;


/**
 * sql方言
 */
public interface SQLDialect {
    /**
     * 封装字段名
     * @param fieldName
     * @return
     */
    String wrapFieldName(String fieldName);
    
    /**
     * 解析字段名
     * @param fieldName
     * @return
     */

    String parseFieldName(String fieldName);
    
    
    /**
     * 封装表名
     * @param name
     * @return
     */
    String wrapTableName(String name);
    
    /**
     * 解析表名
     * @param tableName
     * @return
     */
    String parseTableName(String tableName);
}
