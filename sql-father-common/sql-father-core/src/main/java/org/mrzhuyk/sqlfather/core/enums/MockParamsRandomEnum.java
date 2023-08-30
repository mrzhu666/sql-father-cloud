package org.mrzhuyk.sqlfather.core.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MockParamsRandomEnum {
    
    STRING("字符串"),
    NAME("人名"),
    CITY("城市"),
    URL("网址"),
    EMAIL("邮箱"),
    IP("IP"),
    INTEGER("整数"),
    DECIMAL("小数"),
    UNIVERSITY("大学"),
    DATE("日期"),
    TIMESTAMP("时间戳"),
    PHONE("手机号");
    
    private final String value;
    
    MockParamsRandomEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    
    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(MockParamsRandomEnum::getValue).collect(Collectors.toList());
    }
    
    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static MockParamsRandomEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (MockParamsRandomEnum mockEnum : MockParamsRandomEnum.values()) {
            if (mockEnum.value.equals(value)) {
                return mockEnum;
            }
        }
        return null;
    }
    
}
