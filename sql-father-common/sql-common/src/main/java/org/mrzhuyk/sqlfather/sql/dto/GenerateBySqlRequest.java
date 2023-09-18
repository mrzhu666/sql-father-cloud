package org.mrzhuyk.sqlfather.sql.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * 根据sql生成表概要
 */
@Data
public class GenerateBySqlRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    
    private String sql;
}
