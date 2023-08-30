package org.mrzhuyk.sqlfather.core.generator.data;

import org.mrzhuyk.sqlfather.core.enums.MockParamsRandomEnum;
import org.mrzhuyk.sqlfather.core.generator.utils.FakerUtils;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 随机数据生成，根据库提供的常用模拟数据
 */
public class RandomDataGenerator implements DataGenerate{
    @Override
    public List<String> doGenerate(TableSchema.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            MockParamsRandomEnum mockParamsRandomEnum = Optional.ofNullable(MockParamsRandomEnum.getEnumByValue(mockParams)).orElse(MockParamsRandomEnum.STRING);
            String randomValue = FakerUtils.getRandomValue(mockParamsRandomEnum);
            list.add(randomValue);
        }
        
        return list;
    }
}
