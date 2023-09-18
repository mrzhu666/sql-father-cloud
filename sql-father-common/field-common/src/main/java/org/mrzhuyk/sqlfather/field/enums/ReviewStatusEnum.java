package org.mrzhuyk.sqlfather.field.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核状态枚举
 */
public enum ReviewStatusEnum {
    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2),
    ;
    private final String text;
    private final int value;
    
    ReviewStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }
    
    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
    
    /**
     * 判断值是否包含
     * @param value
     * @return
     */
    public static boolean valid(Integer value) {
        if(value==null) return false;
        return getValues().contains(value);
    }
    
    public String getText() {
        return text;
    }
    
    public int getValue() {
        return value;
    }
}
