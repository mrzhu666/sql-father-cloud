package org.mrzhuyk.sqlfather.sql.vo;

import lombok.Data;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 所有生成内容
 */
@Data
public class GenerateVO implements Serializable {
    private TableSchema tableSchema;
    
    private String createSql;
    
    /**
     * 模拟数据
     */
    private List<Map<String, Object>> dataList;
    
    private String insertSql;
    
    private String dataJson;
    
    private String javaEntityCode;
    
    private String javaObjectCode;
    
    private String typescriptTypeCode;
    
    private static final long serialVersionUID = 7122637163626243606L;
}
