package org.mrzhuyk.sqlfather.core.generator.dialect;

public class MySQLSQLDialect implements SQLDialect {
    @Override
    public String wrapFieldName(String fieldName) {
        return null;
    }
    
    @Override
    public String parseFieldName(String fieldName) {
        return null;
    }
    
    @Override
    public String wrapTableName(String name) {
        return null;
    }
    
    @Override
    public String parseTableName(String tableName) {
        return null;
    }
}
