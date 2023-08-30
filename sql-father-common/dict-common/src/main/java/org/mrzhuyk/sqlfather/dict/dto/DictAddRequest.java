package org.mrzhuyk.sqlfather.dict.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 创建词典请求
 */
@Data
public class DictAddRequest implements Serializable {
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
