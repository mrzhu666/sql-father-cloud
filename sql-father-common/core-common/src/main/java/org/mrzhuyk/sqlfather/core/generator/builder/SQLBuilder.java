package org.mrzhuyk.sqlfather.core.generator.builder;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.dialect.MySQLDialect;
import org.mrzhuyk.sqlfather.core.generator.dialect.SQLDialect;
import org.mrzhuyk.sqlfather.core.generator.dialect.SQLDialectFactory;
import org.mrzhuyk.sqlfather.sql.enums.FieldTypeEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SQL生成器，生成SQL语句
 * 支持方言，策略模式
 *
 * 这里偷懒了，系统没有定义具体使用哪种数据库的属性
 */
@Slf4j
public class SQLBuilder {
    private SQLDialect sqlDialect;
    
    
    public SQLBuilder() {
        this.sqlDialect = SQLDialectFactory.getDialect(MySQLDialect.class.getName());
    }
    
    public SQLBuilder(SQLDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
    
    /**
     * 创建表的sql语句
     * @param tableSchema
     * @return
     */
    public String buildCreateTableSql(TableSchema tableSchema) {
        // 构造模板
        String template = "%s\n"
            + "create table if not exists %s\n"
            + "(\n"
            + "%s\n"
            + ") %s;";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造sql注释
        String tableComment = tableSchema.getTableComment();
        if (StringUtils.isBlank(tableComment)) {
            tableComment = tableName;
        }
        String tablePrefixComment = String.format("-- %s", tableComment);
        // 构造表comment
        String tableSuffixComment = String.format("comment '%s'", tableComment);
        // 构造表字段
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        StringBuilder fieldBuilder = new StringBuilder();
        int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            TableSchema.Field field = fieldList.get(i);
            fieldBuilder.append(buildCreateFieldSql(field));
            if (i != fieldSize - 1) {
                fieldBuilder.append(",");
                fieldBuilder.append("\n");
            }
        }
        String fieldString = fieldBuilder.toString();
        // 填充模板
        String result = String.format(template, tablePrefixComment, tableName, fieldString, tableSuffixComment);
        log.info("sql result = " + result);
        return result;
    }
    
    /**
     * 生成创建字段的sql
     * @param field
     * @return
     */
    public String buildCreateFieldSql(TableSchema.Field field) {
        if (field == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String fieldName = sqlDialect.wrapFieldName(field.getFieldName());
        String fieldType = field.getFieldType();
        String defaultValue = field.getDefaultValue();
        boolean notNull = field.isNotNull();
        String comment = field.getComment();
        String onUpdate = field.getOnUpdate();
        boolean primaryKey = field.isPrimaryKey();
        boolean autoIncrement = field.isAutoIncrement();
        // e.g. column_name int default 0 not null auto_increment comment '注释' primary key,
        StringBuilder fieldBuilder = new StringBuilder();
        // 字段名
        fieldBuilder.append(fieldName);
        // 字段类型
        fieldBuilder.append(" ").append(fieldType);
        // 默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldBuilder.append(" ").append("default ").append(getValueStr(field, defaultValue));
        }
        // 是否非空
        fieldBuilder.append(" ").append(notNull ? "not null" : "null");
        // 是否自增
        if (autoIncrement) {
            fieldBuilder.append(" ").append("auto_increment");
        }
        // 附加条件
        if (StringUtils.isNotBlank(onUpdate)) {
            fieldBuilder.append(" ").append("on update ").append(onUpdate);
        }
        // 注释
        if (StringUtils.isNotBlank(comment)) {
            fieldBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        // 是否为主键
        if (primaryKey) {
            fieldBuilder.append(" ").append("primary key");
        }
        return fieldBuilder.toString();
    }
    
    
    /**
     * 构造插入数据sql
     * e.g. INSERT INTO report (id, content) VALUES (1, '这个有点问题吧');
     *
     * @param tableSchema 表概要
     * @param dataList 模拟数据，所有数据都会使用
     * @return 插入数据sql
     */
    public String buildInsertSql(TableSchema tableSchema, List<Map<String, Object>> dataList) {
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 构造表名
        String tableName = sqlDialect.wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表字段
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        // 过滤不模拟的字段
        fieldList = fieldList.stream().filter(field -> {
            MockTypeEnum typeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType())).orElse(MockTypeEnum.NONE);
            return !typeEnum.equals(MockTypeEnum.NONE);
        }).collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        int size = dataList.size();
        for (int i = 0; i < size; i++) {
            Map<String, Object> dataRow = dataList.get(i);
            //获取字段
            String fieldName = fieldList.stream()
                .map(field -> sqlDialect.wrapFieldName(field.getFieldName()))
                .collect(Collectors.joining(","));
            //获取模拟值
            String data = fieldList.stream()
                .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                .collect(Collectors.joining(","));
            //插入一行数据
            String insertRow = String.format(template, tableName, fieldName, data);
            builder.append(insertRow);
            if (i != size - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    
    
    /**
     * 根据字段类型，字符串化value
     * @param field
     * @param value
     * @return
     */
    public static String getValueStr(TableSchema.Field field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
            .orElse(FieldTypeEnum.TEXT);
        String result = String.valueOf(value);
        switch (fieldTypeEnum) {
            case DATETIME:
            case TIMESTAMP:
                return result.equalsIgnoreCase("CURRENT_TIMESTAMP") ? result : String.format("'%s'", value);
            case DATE:
            case TIME:
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case BINARY:
            case VARBINARY:
                return String.format("'%s'", value);
            default:
                return result;
        }
    }
    
}
