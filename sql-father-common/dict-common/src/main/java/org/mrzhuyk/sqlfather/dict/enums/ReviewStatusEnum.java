package org.mrzhuyk.sqlfather.dict.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 词条审核状态
 *  审核通过的词库会变成公开的
 */
public enum ReviewStatusEnum {
    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);
    
    ReviewStatusEnum(String test, int value) {
        this.test = test;
        this.value = value;
    }
    
    private final String test;
    
    private final int value;
    
    /**
     * 获取值列表
     * @return
     */
    public static List<Integer> getValues(){
        return Arrays.stream(values()).map(ReviewStatusEnum::getValue).collect(Collectors.toList());
    }
    
    public String getTest() {
        return test;
    }
    
    public int getValue() {
        return value;
    }
}
