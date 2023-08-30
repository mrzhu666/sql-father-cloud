package org.mrzhuyk.sqlfather.sql.dto;


import lombok.Data;

import java.util.List;

/**
 * Java 实体生成封装类
 * 和JavaObjectGenerateDTO有什么区别
 */
@Data
public class JavaEntityGenerateDTO {
    
    /**
     * 类名
     */
    private String classname;
    
    /**
     * 类注释
     */
    private String classComment;
    
    
    /**
     * 列信息列表
     */
    private List<FieldDTO> fieldList;
    
    /**
     * 列信息
     */
    public static class FieldDTO{
        /**
         * 字段名
         */
        private String fieldName;
        
        /**
         * java类型
         */
        private String javaType;
        
        
        /**
         * 注释（字段中文名）
         */
        private String comment;
    }
}
