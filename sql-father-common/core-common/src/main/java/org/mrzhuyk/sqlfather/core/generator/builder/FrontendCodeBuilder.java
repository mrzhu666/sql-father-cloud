package org.mrzhuyk.sqlfather.core.generator.builder;

import cn.hutool.core.util.StrUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.generator.config.FreeMarkerConfig;
import org.mrzhuyk.sqlfather.sql.dto.TypescriptTypeGenerateDTO;
import org.mrzhuyk.sqlfather.sql.enums.FieldTypeEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * typescript前端代码生成
 */
public class FrontendCodeBuilder {
    private static Configuration cfg = FreeMarkerConfig.getCfg();
    
    /**
     * typescript对象
     * @param tableSchema
     * @return
     */
    public static String buildTypeScriptTypeCode(TableSchema tableSchema) throws IOException, TemplateException {
        TypescriptTypeGenerateDTO generateDTO = new TypescriptTypeGenerateDTO();
        String tableName = tableSchema.getTableName();
        String tableComment = tableSchema.getTableComment();
        String upperCamelTableName = StringUtils.capitalize(StrUtil.toCamelCase(tableName));
        // 类名为大写的表名
        generateDTO.setClassName(upperCamelTableName);
        // 类注释为表注释 > 表名
        generateDTO.setClassComment(Optional.ofNullable(tableComment).orElse(upperCamelTableName));
        // 依次填充每一列
        List<TypescriptTypeGenerateDTO.FieldDTO> fieldDTOList = new ArrayList<>();
        for (TableSchema.Field field : tableSchema.getFieldList()) {
            TypescriptTypeGenerateDTO.FieldDTO fieldDTO = new TypescriptTypeGenerateDTO.FieldDTO();
            fieldDTO.setFieldName(StrUtil.toCamelCase(field.getFieldName()));
            fieldDTO.setComment(field.getComment());
            FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType())).orElse(FieldTypeEnum.TEXT);
            fieldDTO.setTypescriptType(fieldTypeEnum.getTypescriptType());
            fieldDTOList.add(fieldDTO);
        }
        generateDTO.setFieldList(fieldDTOList);
        StringWriter stringWriter = new StringWriter();
        Template temp = cfg.getTemplate("typescript_type.ftl");
        temp.process(generateDTO, stringWriter);
        return stringWriter.toString();
    }
}
