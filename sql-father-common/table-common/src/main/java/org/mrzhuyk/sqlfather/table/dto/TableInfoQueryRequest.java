package org.mrzhuyk.sqlfather.table.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mrzhuyk.sqlfather.core.dto.PageRequest;

import java.io.Serializable;

/**
 * 表查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TableInfoQueryRequest extends PageRequest implements Serializable {
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
