package org.mrzhuyk.sqlfather.core.generator.data;

import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 数据工厂
 * 工厂+单例
 *  根据枚举类型获取对应的数据生成类
 */
public class DataGenerateFactory {
    private final static Map<MockTypeEnum, DataGenerate> mockTypeDataGeneratorMap = new HashMap<MockTypeEnum, DataGenerate>(){{
        put(MockTypeEnum.NONE, new DefaultDataGenerator());
        put(MockTypeEnum.FIXED, new FixedDataGenerator());
        put(MockTypeEnum.RANDOM, new RandomDataGenerator());
        put(MockTypeEnum.RULE, new RuleDataGenerator());
        put(MockTypeEnum.DICT, new DictDataGenerator());
        put(MockTypeEnum.INCREASE, new IncreaseDataGenerator());
    }};
    
    /**
     * 根据枚举类型获取对应的数据生成类
     * @param mockTypeEnum
     * @return
     */
    public static DataGenerate getByMockEnum(MockTypeEnum mockTypeEnum) {
        MockTypeEnum typeEnum = Optional.ofNullable(mockTypeEnum).orElse(MockTypeEnum.NONE);
        return mockTypeDataGeneratorMap.get(typeEnum);
    }
}
