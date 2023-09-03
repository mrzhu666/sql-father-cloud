package org.mrzhuyk.sqlfather.sql.enums;


import org.apache.commons.lang3.StringUtils;


/**
 * 枚举字段类型
 */
public enum FieldTypeEnum {
    TINYINT("tinyint", "Integer", "number"),
    SMALLINT("smallint", "Integer", "number"),
    MEDIUMINT("mediumint", "Integer", "number"),
    INT("int", "Integer", "number"),
    BIGINT("bigint", "Long", "number"),
    FLOAT("float", "Double", "number"),
    DOUBLE("double", "Double", "number"),
    DECIMAL("decimal", "BigDecimal", "number"),
    DATE("date", "Date", "Date"),
    TIME("time", "Time", "Date"),
    YEAR("year", "Integer", "number"),
    DATETIME("datetime", "Date", "Date"),
    TIMESTAMP("timestamp", "Long", "number"),
    CHAR("char", "String", "string"),
    VARCHAR("varchar", "String", "string"),
    TINYTEXT("tinytext", "String", "string"),
    TEXT("text", "String", "string"),
    MEDIUMTEXT("mediumtext", "String", "string"),
    LONGTEXT("longtext", "String", "string"),
    TINYBLOB("tinyblob", "byte[]", "string"),
    BLOB("blob", "byte[]", "string"),
    MEDIUMBLOB("mediumblob", "byte[]", "string"),
    LONGBLOB("longblob", "byte[]", "string"),
    BINARY("binary", "byte[]", "string"),
    VARBINARY("varbinary", "byte[]", "string"),
    ;
    private final String value;
    
    private final String javaType;
    
    private final String typescriptType;
    
    FieldTypeEnum(String value, String javaType, String typescriptType) {
        this.value = value;
        this.javaType = javaType;
        this.typescriptType = typescriptType;
    }
    
    /**
     * 根据value获取enum
     * @param value
     * @return
     */
    public static FieldTypeEnum getEnumByValue(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        for (FieldTypeEnum fieldTypeEnum : FieldTypeEnum.values()) {
            if (StringUtils.equals(fieldTypeEnum.value, value)) {
                return fieldTypeEnum;
            }
        }
        return null;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getJavaType() {
        return javaType;
    }
    
    public String getTypescriptType() {
        return typescriptType;
    }
}
