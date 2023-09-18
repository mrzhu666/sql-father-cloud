package org.mrzhuyk.sqlfather.core.generator;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import freemarker.template.TemplateException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.builder.*;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.GenerateVO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据生成器，门面模式，统一生成
 */
public class GeneratorFacade {
    /**
     * 根据表信息生成所有代码和数据
     * @param tableSchema 表概要
     * @return 表
     */
    @SneakyThrows
    public static GenerateVO generateAll(TableSchema tableSchema) {
        // 校验表
        validSchema(tableSchema);
        // 构建表sql
        SQLBuilder sqlBuilder = new SQLBuilder();
        String createTableSql = sqlBuilder.buildCreateTableSql(tableSchema);
        Integer mockNum = tableSchema.getMockNum();
        // 模拟数据
        List<Map<String, Object>> dataList = DataBuilder.generateData(tableSchema, mockNum);
        // 插入数据sql
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, dataList);
        // 生成 数据json
        String dataJson = JsonBuilder.buildJson(dataList);
        // 生成 java实体代码
        String javaEntity = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        // 生成 java构造代码
        String javaObject = JavaCodeBuilder.buildJavaObjectCode(tableSchema, dataList);
        // 生成 typescript对象代码
        String typeScriptCode = FrontendCodeBuilder.buildTypeScriptTypeCode(tableSchema);
        //封装返回
        GenerateVO generateVO = new GenerateVO();
        generateVO.setTableSchema(tableSchema);
        generateVO.setCreateSql(createTableSql);
        generateVO.setDataList(dataList);
        generateVO.setInsertSql(insertSql);
        generateVO.setDataJson(dataJson);
        generateVO.setJavaEntityCode(javaEntity);
        generateVO.setJavaObjectCode(javaObject);
        generateVO.setTypescriptTypeCode(typeScriptCode);
        return generateVO;
    }
    
    /**
     * 校验表
     * @param tableSchema
     */
    public static void validSchema(TableSchema tableSchema) {
        if (tableSchema == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "数据为空");
        }
        String tableName = tableSchema.getTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "表明为空");
        }
        // 模拟数据数量
        Integer mockNum = tableSchema.getMockNum();
        // 默认生成 20 条
        if (tableSchema.getMockNum() == null) {
            tableSchema.setMockNum(20);
            mockNum = 20;
        }
        if (mockNum > 100 || mockNum < 10) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "生成条数设置错误");
        }
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR,"字段列表不能为空");
        }
        for (TableSchema.Field field : fieldList) {
            validFiled(field);
        }
        
    }
    
    /**
     * 校验字段
     *  校验字段名和字段类型不为空
     * @param field 字段
     */
    public static void validFiled(TableSchema.Field field) {
        String fieldName = field.getFieldName();
        String fieldType = field.getFieldType();
        if (StringUtils.isBlank(fieldName)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "字段名不能为空");
        }
        if (StringUtils.isBlank(fieldType)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "字段类型不能为空");
        }
    }
}
