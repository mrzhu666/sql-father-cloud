package org.mrzhuyk.sqlfather.table.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 添加请求
 */
@Data
public class TableInfoAddRequest implements Serializable {
    /**
     * 名称
     */
    private String name;
    
    /**
     * 内容
     */
    private String content;
    
    private static final long serialVersionUID = 1L;
}
