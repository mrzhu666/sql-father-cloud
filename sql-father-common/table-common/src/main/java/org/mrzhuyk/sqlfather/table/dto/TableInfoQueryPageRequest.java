package org.mrzhuyk.sqlfather.table.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mrzhuyk.sqlfather.core.constant.CommonConstant;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class TableInfoQueryPageRequest implements Serializable {

    /**
     * 状态（0-待审核, 1-通过, 2-拒绝）
     */
    @Schema(name = "reviewStatus", description = "状态(0-待审核, 1-通过, 2-拒绝)", defaultValue = "1")
    private Integer reviewStatus;
    
    /**
     * 当前页号
     */
    @Schema(name = "current", description = "当前页号", defaultValue = "1")
    private long current = 1;
    
    /**
     * 页面大小
     */
    @Schema(name = "pageSize", description = "页面大小", defaultValue = "10")
    private long pageSize = 10;
    
    /**
     * 排序字段
     */
    @Schema(name = "sortField", description = "排序字段", defaultValue = "createTime")
    private String sortField;
    
    /**
     * 排序顺序（默认升序）
     */
    @Schema(name = "sortOrder", description = "排序顺序(默认升序)", defaultValue = CommonConstant.SORT_ORDER_ASC)
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
    
    private static final long serialVersionUID = 1L;
}
