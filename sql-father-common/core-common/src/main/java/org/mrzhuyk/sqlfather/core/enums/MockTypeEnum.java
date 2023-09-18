package org.mrzhuyk.sqlfather.core.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模拟数据类型
 */
public enum MockTypeEnum {
    NONE("不模拟"),
    INCREASE("递增"),
    FIXED("固定"),
    RANDOM("随机"),
    RULE("规则"),
    DICT("词库"),
    ;
    
    
    private final String value;
    
    MockTypeEnum(String value) {
        this.value = value;
    }
    
    /**
     * 获取值列表
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(MockTypeEnum::getValue).collect(Collectors.toList());
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据值获取枚举
     * @param value
     * @return
     */
    public static MockTypeEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (MockTypeEnum typeEnum : MockTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}
