package org.mrzhuyk.sqlfather.core.generator.builder;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.generator.data.DataGenerate;
import org.mrzhuyk.sqlfather.core.generator.data.DataGenerateFactory;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.*;

/**
 * 数据生成器
 *  根据字段的模拟类型进行数据生成
 */
public class DataBuilder {
    /**
     * 生成数据
     * @param tableSchema
     * @param rowNum
     * @return 数据格式：数量<字段,数据>
     */
    public static List<Map<String, Object>> generateData(TableSchema tableSchema, int rowNum) {
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>());
        }
        // 依次生成每一列
        for (TableSchema.Field field : fieldList) {
            MockTypeEnum mockTypeEnum = Optional
                .ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                .orElse(MockTypeEnum.NONE);  // 获取模拟类型
            DataGenerate dataGenerate = DataGenerateFactory.getByMockEnum(mockTypeEnum);
            List<String> dataList = dataGenerate.doGenerate(field, rowNum);
            String fieldName = field.getFieldName();
            // 填充结果列表
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName, dataList.get(i));
                }
            }
        }
        return resultList;
    }
}
