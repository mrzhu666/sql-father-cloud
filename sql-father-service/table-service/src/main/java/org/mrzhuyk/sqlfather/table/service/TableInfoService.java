package org.mrzhuyk.sqlfather.table.service;

import org.mrzhuyk.sqlfather.table.dto.TableInfoQueryRequest;
import org.mrzhuyk.sqlfather.table.po.TableInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author mrzhu
* @description 针对表【table_info(表信息)】的数据库操作Service
* @createDate 2023-08-30 21:55:00
*/
public interface TableInfoService extends IService<TableInfo> {
    /**
     * 校验并处理
     *  抛出异常
     * @param tableInfo
     * @param add 是否为创建校验
     */
    void validAndHandleTableInfo(TableInfo tableInfo, boolean add);
}
