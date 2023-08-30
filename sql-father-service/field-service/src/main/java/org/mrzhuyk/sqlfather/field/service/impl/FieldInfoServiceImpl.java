package org.mrzhuyk.sqlfather.field.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.field.service.FieldInfoService;
import org.mrzhuyk.sqlfather.field.mapper.FieldInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author mrzhu
* @description 针对表【field_info(字段信息)】的数据库操作Service实现
* @createDate 2023-08-30 21:13:22
*/
@Service
public class FieldInfoServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo>
    implements FieldInfoService{

}




