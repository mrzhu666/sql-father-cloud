package org.mrzhuyk.sqlfather.core.generator.data;

import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;

/**
 * 固定数字生成
 */
public class FixedDataGenerator implements DataGenerate{
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        if (StringUtils.isBlank(mockParams)) {
            mockParams = "6";
        }
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            list.add(mockParams);
        }
        return list;
    }
}
