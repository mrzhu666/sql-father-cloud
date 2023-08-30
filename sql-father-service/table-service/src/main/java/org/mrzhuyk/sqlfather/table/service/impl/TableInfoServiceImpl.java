package org.mrzhuyk.sqlfather.table.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mrzhuyk.sqlfather.table.po.TableInfo;
import org.mrzhuyk.sqlfather.table.service.TableInfoService;
import org.mrzhuyk.sqlfather.table.mapper.TableInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author mrzhu
* @description 针对表【table_info(表信息)】的数据库操作Service实现
* @createDate 2023-08-30 21:55:00
*/
@Service
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo>
    implements TableInfoService{

}




