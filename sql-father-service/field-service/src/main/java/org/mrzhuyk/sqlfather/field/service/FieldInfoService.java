package org.mrzhuyk.sqlfather.field.service;

import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;

import java.util.List;

/**
* @author mrzhu
* @description 针对表【field_info(字段信息)】的数据库操作Service
* @createDate 2023-08-30 21:13:22
*/
public interface FieldInfoService extends IService<FieldInfo> {
    /**
     * 校验并处理
     *
     * @param fieldInfo
     * @param add 是否为创建校验
     */
    void validAndHandleFieldInfo(FieldInfo fieldInfo, boolean add);
    
    Boolean batchAddFieldInfo(List<TableSchema.Field> fieldList);
}
