package org.mrzhuyk.sqlfather.core.generator.builder;

import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.config.FreeMarkerConfig;
import org.mrzhuyk.sqlfather.sql.dto.JavaEntityGenerateDTO;
import org.mrzhuyk.sqlfather.sql.dto.JavaEntityGenerateDTO.FieldDTO;
import org.mrzhuyk.sqlfather.sql.dto.JavaObjectGenerateDTO;
import org.mrzhuyk.sqlfather.sql.enums.FieldTypeEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;



/**
 * java代码生成
 */
public class JavaCodeBuilder {
    
    private static final Configuration cfg=FreeMarkerConfig.getCfg();

    public static void main(String[] args) throws TemplateException, IOException {
        TableSchema.Field field = new TableSchema.Field();
        field.setFieldName("test_field");
        field.setFieldType("tinytext");
        field.setDefaultValue("");
        field.setNotNull(false);
        field.setComment("测试字段");
        field.setPrimaryKey(false);
        field.setAutoIncrement(false);
        field.setMockType("随机");
        field.setMockParams("字符串");
        field.setOnUpdate("");

        List<TableSchema.Field> fieldList=new ArrayList<>();
        fieldList.add(field);

        TableSchema tableSchema = new TableSchema();
        tableSchema.setDbName("test");
        tableSchema.setTableName("test");
        tableSchema.setTableComment("test");
        tableSchema.setMockNum(1);
        tableSchema.setFieldList(fieldList);

        String result = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        System.out.println(result);
    }

    /**
     * 生成java实体代码
     * @param tableSchema 表概要
     * @return 生成的java代码
     */
    public static String buildJavaEntityCode(TableSchema tableSchema) throws IOException, TemplateException {
        // 传递参数
        JavaEntityGenerateDTO javaEntityGenerateDTO = new JavaEntityGenerateDTO();
        String tableName = tableSchema.getTableName();
        String tableComment = tableSchema.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        // 类名为大写的表名
        javaEntityGenerateDTO.setClassName(upperCamelTableName);
        // 类注释为表注释 > 表名
        javaEntityGenerateDTO.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));
        // 依次填充每一列
        List<FieldDTO> fieldDTOList = new ArrayList<>();
        for (TableSchema.Field field : tableSchema.getFieldList()) {
            FieldDTO fieldDTO = new FieldDTO();
            fieldDTO.setComment(field.getComment());
            fieldDTO.setFieldName(field.getFieldName());
            // 字段类型获取java类型
            String fieldType = field.getFieldType();
            FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(fieldType)).orElse(FieldTypeEnum.TEXT);
            fieldDTO.setJavaType(fieldTypeEnum.getJavaType());
            fieldDTOList.add(fieldDTO);
        }
        javaEntityGenerateDTO.setFieldList(fieldDTOList);
        StringWriter stringWriter = new StringWriter();
        Template template = cfg.getTemplate("java_entity.ftl");
        template.process(javaEntityGenerateDTO, stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * 实例构造的java代码，每个属性设置值。
     * @param tableSchema 表概要
     * @param dataList 模拟数据，只使用其中一个
     * @return java代码
     */
    public static String buildJavaObjectCode(TableSchema tableSchema, List<Map<String, Object>> dataList) throws IOException, TemplateException {
        if (CollectionUtils.isEmpty(dataList)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "缺少示例数据");
        }
        // 传递参数
        JavaObjectGenerateDTO javaObjectGenerateDTO = new JavaObjectGenerateDTO();
        String tableName = tableSchema.getTableName();
        String camelTableName = StrUtil.toCamelCase(tableName);
        // 类名为大写的表名
        javaObjectGenerateDTO.setClassName(StringUtils.capitalize(camelTableName));
        // 变量名为表名
        javaObjectGenerateDTO.setObjectName(camelTableName);
        // 填充每一列
        Map<String, Object> fillData = dataList.get(0);
        List<JavaObjectGenerateDTO.FieldDTO> fieldDTOList = new ArrayList<>();
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        // 过滤不模拟的字段
        fieldList = fieldList.stream().filter(field -> {
            MockTypeEnum typeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType())).orElse(MockTypeEnum.NONE);
            return !typeEnum.equals(MockTypeEnum.NONE);
        }).collect(Collectors.toList());
        for (TableSchema.Field field : fieldList) {
            JavaObjectGenerateDTO.FieldDTO fieldDTO = new JavaObjectGenerateDTO.FieldDTO();
            // 驼峰字段名
            String fieldName = field.getFieldName();
            fieldDTO.setSetMethod(StrUtil.toCamelCase("set_" + fieldName));
            fieldDTO.setValue(getValueStr(field, fillData.get(fieldName)));
            fieldDTOList.add(fieldDTO);
        }
        javaObjectGenerateDTO.setFieldList(fieldDTOList);
        StringWriter stringWriter = new StringWriter();
        Template temp = cfg.getTemplate("java_object.ftl");
        temp.process(javaObjectGenerateDTO, stringWriter);
        return stringWriter.toString();
    }
    
    /**
     * 根据字段的值转换为对应字符串
     * @param field 字段
     * @param value 值
     * @return 字符串
     */
    public static String getValueStr(TableSchema.Field field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
            .orElse(FieldTypeEnum.TEXT);
        switch (fieldTypeEnum) {
            case DATE:
            case TIME:
            case DATETIME:
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
                return String.format("\"%s\"", value);
            default:
                return String.valueOf(value);
        }
    }
}
