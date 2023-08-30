package org.mrzhuyk.sqlfather.dict.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mrzhuyk.sqlfather.core.dto.PageRequest;

import java.io.Serializable;

/**
 * 词典查询，包含分页查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DictQueryRequest extends PageRequest implements Serializable {
    
    /**
     * 名称
     */
    private String name;
    
    /**
     * 内容，支持模糊查询
     */
    private String content;
    
    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    private Integer reviewStatus;
    
    /**
     * 创建用户 id
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
}
