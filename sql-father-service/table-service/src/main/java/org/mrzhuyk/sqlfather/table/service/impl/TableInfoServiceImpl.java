package org.mrzhuyk.sqlfather.table.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.GeneratorFacade;
import org.mrzhuyk.sqlfather.field.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.field.feign.FieldClient;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.table.po.TableInfo;
import org.mrzhuyk.sqlfather.table.service.TableInfoService;
import org.mrzhuyk.sqlfather.table.mapper.TableInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mrzhu
 * @description 针对表【table_info(表信息)】的数据库操作Service实现
 * @createDate 2023-08-30 21:55:00
 */
@Service
@Slf4j
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo> implements TableInfoService {
    private final static Gson GSON = new Gson();
    @Resource
    private FieldClient fieldClient;
    
    @Resource
    private TableInfoMapper tableInfoMapper;
    
    /**
     * 校验表信息
     * @param add 是否创建表
     */
    @Override
    public void validAndHandleTableInfo(TableInfo tableInfo, boolean add) {
        if (tableInfo == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String name = tableInfo.getName();
        String content = tableInfo.getContent();
        Integer reviewStatus = tableInfo.getReviewStatus();
        
        // 创建时，所有参数必须非空
        if (add && StringUtils.isAnyBlank(name, content)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        if (StringUtils.isNotBlank(name) && name.length() > 30) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "名称过长");
        }
        // reviewStatus为空时代表创建
        if (reviewStatus != null && !ReviewStatusEnum.valid(reviewStatus)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        if (StringUtils.isNotBlank(content)) {
            if (content.length() > 20000) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "内容过长");
            }
            // 校验字段内容
            try {
                TableSchema tableSchema = GSON.fromJson(content, TableSchema.class);
                GeneratorFacade.validSchema(tableSchema);
            } catch (Exception e) {
                throw new BizException(ErrorEnum.PARAMS_ERROR, "内容格式错误");
            }
        }
    }
    
    
    @Override
    @GlobalTransactional(timeoutMills = 300_000, name = "addTableField")
    public boolean addTableAndField(TableInfo tableInfo) {
        validAndHandleTableInfo(tableInfo, true);
        TableSchema tableSchema = GSON.fromJson(tableInfo.getContent(), TableSchema.class);
        
        List<TableSchema.Field> fieldList = tableSchema.getFieldList();
        fieldClient.batchAddFieldInfo(fieldList);
        
        LambdaQueryWrapper<TableInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
            .eq(TableInfo::getName, tableInfo.getName())
            .eq(TableInfo::getUserId, tableInfo.getUserId());
        
        TableInfo selectOne = tableInfoMapper.selectOne(queryWrapper);
        if (selectOne != null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "提交过相同的表名称：" + tableInfo.getName());
        }
        
        boolean save = save(tableInfo);
        return save;
    }
}




