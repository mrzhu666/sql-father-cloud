package org.mrzhuyk.sqlfather.field.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.GeneratorFacade;
import org.mrzhuyk.sqlfather.field.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.field.service.FieldInfoService;
import org.mrzhuyk.sqlfather.field.mapper.FieldInfoMapper;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrzhu
 * @description 针对表【field_info(字段信息)】的数据库操作Service实现
 * @createDate 2023-08-30 21:13:22
 */
@Service
public class FieldInfoServiceImpl extends ServiceImpl<FieldInfoMapper, FieldInfo>
    implements FieldInfoService {
    private final static Gson GSON = new Gson();
    
    @Resource
    UserClient userClient;
    
    
    /**
     * 校验字段属性
     */
    @Override
    public void validAndHandleFieldInfo(FieldInfo fieldInfo, boolean add) {
        if (fieldInfo == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String name = fieldInfo.getName();
        String content = fieldInfo.getContent();
        Integer reviewStatus = fieldInfo.getReviewStatus();
        // 创建时，所有参数必须非空
        if (add && StringUtils.isAnyBlank(name, content)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        if (StringUtils.isBlank(name) || name.length() > 30) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isBlank(content)) {
            if (content.length() > 20000) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "内容过长");
            }
            // 校验字段内容
            try {
                TableSchema.Field field = GSON.fromJson(content, TableSchema.Field.class);
                GeneratorFacade.validFiled(field);
                // 填充 fieldName
                fieldInfo.setFieldName(field.getFieldName());
            } catch (Exception e) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "内容格式错误");
            }
        }
        // 校验审核状态
        if (reviewStatus != null && !ReviewStatusEnum.valid(reviewStatus)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean batchAddFieldInfo(List<TableSchema.Field> fieldList) {
        UserVO loginUser = userClient.getLoginUser();
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        for (TableSchema.Field field : fieldList) {
            FieldInfo fieldInfo = new FieldInfo();
            
            LambdaQueryWrapper<FieldInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper
                .eq(FieldInfo::getName, field.getComment())
                .eq(FieldInfo::getUserId, loginUser.getId());
            if (getOne(queryWrapper) != null) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "提交过相同的字段名称："+field.getComment());
            }
            
            fieldInfo.setName(field.getComment());  // 字段名称
            fieldInfo.setContent(GSON.toJson(field));
            fieldInfo.setUserId(loginUser.getId());
            fieldInfoList.add(fieldInfo);
        }
        
        boolean b = saveBatch(fieldInfoList);
        
        return b;
    }
}




