package org.mrzhuyk.sqlfather.core.generator.builder;



import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlCreateTableParser;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.dialect.MySQLDialect;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.sql.enums.FieldTypeEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 表概要生成器
 *      作用是根据一些信息构建填充表
 */
@Slf4j
public class TableSchemaBuilder {
    private final static Gson GSON = new Gson();
    
    
    private static final MySQLDialect sqlDialect=new MySQLDialect();
    
    /**
     * 日期格式
     */
    private static final String[] DATE_PATTERNS = {"yyyy-MM-dd", "yyyy年MM月dd日", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};
    
    /**
     * 智能构建
     *      根据输入的名称，自动导入可能的字段，即自动填充字段
     *      字段服务传入参数
     * @param words 用户输入的可能的单词
     * @param fieldInfoList 字段数据
     */
    public static TableSchema buildFromAuto(String[] words, List<FieldInfo> fieldInfoList) {
        // 名称 => 字段信息
        Map<String, List<FieldInfo>> nameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getName));
        
        // 字段名称 => 字段信息
        Map<String, List<FieldInfo>> fieldNameFieldInfoMap = fieldInfoList.stream().collect(Collectors.groupingBy(FieldInfo::getFieldName));
        
        TableSchema tableSchema = new TableSchema();
        tableSchema.setTableName("my_table");
        tableSchema.setTableComment("自动生成的表");
        
        List<TableSchema.Field> fieldList = new ArrayList<>();
        for (String word : words) {
            TableSchema.Field field;
            List<FieldInfo> infoList = Optional.ofNullable(nameFieldInfoMap.get(word)).orElse(fieldNameFieldInfoMap.get(word));
            if (CollectionUtils.isNotEmpty(infoList)) {
                field = GSON.fromJson(infoList.get(0).getContent(), TableSchema.Field.class);
                
            } else {
                field = getDefaultField(word);
            }
            fieldList.add(field);
        }
        tableSchema.setFieldList(fieldList);
        return tableSchema;
    }
    
    
    
    /**
     * 根据建表sql构建表
     *  druid工具解析sql语句
     * @param sql SQL语句
     */
    public static TableSchema buildFromSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        try {
            // 解析 SQL
            MySqlCreateTableParser parser = new MySqlCreateTableParser(sql);
            SQLCreateTableStatement sqlCreateTableStatement = parser.parseCreateTable();
            
            TableSchema tableSchema = new TableSchema();
            tableSchema.setDbName(sqlCreateTableStatement.getSchema()); // 数据库名称
            tableSchema.setTableName(sqlDialect.parseTableName(sqlCreateTableStatement.getTableName())); // 表名称
            //去除表注释的首尾符号？
            String tableComment = null;
            if (sqlCreateTableStatement.getComment() != null) {
                tableComment = sqlCreateTableStatement.getComment().toString();
                if (tableComment.length() > 2) {
                    tableComment = tableComment.substring(1, tableComment.length() - 1);
                }
            }
            tableSchema.setTableComment(tableComment);
            //解析字段
            List<TableSchema.Field> fieldList = new ArrayList<>();
            for (SQLTableElement sqlTableElement : sqlCreateTableStatement.getTableElementList()) {
                //主键约束
                if (sqlTableElement instanceof SQLPrimaryKey) {
                    SQLPrimaryKey sqlPrimaryKey = (SQLPrimaryKey) sqlTableElement;
                    //获取主键字段名称
                    String primaryFieldName = sqlDialect.parseFieldName(sqlPrimaryKey.getColumns().get(0).toString());
                    fieldList.forEach(field -> {
                        if (field.getFieldName().equals(primaryFieldName)) {
                            field.setPrimaryKey(true);
                        }
                    });
                //普通字段
                } else if (sqlTableElement instanceof SQLColumnDefinition) {
                    SQLColumnDefinition columnDefinition = (SQLColumnDefinition) sqlTableElement;
                    TableSchema.Field field = new TableSchema.Field();
                    field.setFieldName(sqlDialect.parseFieldName(columnDefinition.getNameAsString()));
                    field.setFieldType(columnDefinition.getDataType().toString());
                    //默认值
                    String defaultValue = null;
                    if (columnDefinition.getDefaultExpr() != null) {
                        defaultValue = columnDefinition.getDefaultExpr().toString();
                    }
                    field.setDefaultValue(defaultValue);
                    field.setNotNull(columnDefinition.containsNotNullConstaint());
                    //字段注释,去除注释的首尾符号？
                    String comment = null;
                    if (columnDefinition.getComment() != null) {
                        comment = columnDefinition.getComment().toString();
                        if (comment.length() > 2) {
                            comment = comment.substring(1, comment.length() - 1);
                        }
                    }
                    field.setComment(comment);
                    field.setPrimaryKey(columnDefinition.isPrimaryKey());
                    field.setAutoIncrement(columnDefinition.isAutoIncrement());
                    String onUpdate = null;
                    if (columnDefinition.getOnUpdate() != null) {
                        onUpdate = columnDefinition.getOnUpdate().toString();
                    }
                    field.setOnUpdate(onUpdate);
                    field.setMockType(MockTypeEnum.NONE.getValue());
                    fieldList.add(field);
                }
            }
            tableSchema.setFieldList(fieldList);
            return tableSchema;
        } catch (Exception e) {
            log.error("sql解析错误",e);
            throw new BizException(ErrorEnum.PARAMS_ERROR, "请确认sql语句是否正确");
        }
    }
    
    
    /**
     * 根据excel构建表
     * @param file 传入的文件
     */
    public static TableSchema buildFromExcel(MultipartFile file) {
        try {
            List<Map<Integer,String>> dataList= EasyExcel.read(file.getInputStream()).sheet().headRowNumber(0).doReadSync();
            if (CollectionUtils.isNotEmpty(dataList)) {
                throw new BizException(ErrorEnum.PARAMS_ERROR,"表格无数据");
            }
            // 第一行表头
            Map<Integer, String> map = dataList.get(0);
            List<TableSchema.Field> fieldList = map.values().stream().map(name -> {
                TableSchema.Field field = new TableSchema.Field();
                field.setFieldName(name);
                field.setComment(name);
                field.setFieldType(FieldTypeEnum.TEXT.getValue());
                return field;
            }).collect(Collectors.toList());
            // 第二行值，根据值设置类型
            if (dataList.size() > 1) {
                Map<Integer, String> dataMap = dataList.get(1);
                for (int i = 0; i < fieldList.size(); i++) {
                    String value = dataMap.get(i);
                    // 根据值判断类型
                    String fieldType = getFieldTypeByValue(value);
                    fieldList.get(i).setFieldType(fieldType);
                }
            }
            TableSchema tableSchema = new TableSchema();
            tableSchema.setFieldList(fieldList);
            return tableSchema;
        } catch (Exception e) {
            log.error("buildFromExcel error", e);
            throw new BizException(ErrorEnum.PARAMS_ERROR, "表格解析错误");
        }
    }
    
    /**
     * 通过值获取字段类型
     *
     * @param value 传入的值
     */
    public static String getFieldTypeByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return FieldTypeEnum.TEXT.getValue();
        }
        // 布尔
        if ("false".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value)) {
            return FieldTypeEnum.TINYINT.getValue();
        }
        // 整数
        if (StringUtils.isNumeric(value)) {
            long number = Long.parseLong(value);
            if (number > Integer.MAX_VALUE) {
                return FieldTypeEnum.BIGINT.getValue();
            }
            return FieldTypeEnum.INT.getValue();
        }
        // 小数
        if (isDouble(value)) {
            return FieldTypeEnum.DOUBLE.getValue();
        }
        // 日期
        if (isDate(value)) {
            return FieldTypeEnum.DATETIME.getValue();
        }
        return FieldTypeEnum.TEXT.getValue();
    }
    
    /**
     * 判断字符串是不是 double 型
     * @param str 传入的字符串
     */
    private static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("[0-9]+[.]?[0-9]*[dD]?");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
    
    /**
     * 判断是否为日期
     * @param str 日期字符串
     */
    private static boolean isDate(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        try {
            DateUtils.parseDate(str, DATE_PATTERNS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取默认字段
     * @param word 传入的字词
     */
    private static TableSchema.Field getDefaultField(String word) {
        final TableSchema.Field field = new TableSchema.Field();
        field.setFieldName(word);
        field.setFieldType("text");
        field.setDefaultValue("");
        field.setNotNull(false);
        field.setComment(word);
        field.setPrimaryKey(false);
        field.setAutoIncrement(false);
        field.setMockType("");
        field.setMockParams("");
        field.setOnUpdate("");
        return field;
    }
}
