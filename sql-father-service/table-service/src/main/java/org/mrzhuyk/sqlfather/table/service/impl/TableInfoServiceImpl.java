package org.mrzhuyk.sqlfather.table.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.GeneratorFacade;
import org.mrzhuyk.sqlfather.field.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.table.po.TableInfo;
import org.mrzhuyk.sqlfather.table.service.TableInfoService;
import org.mrzhuyk.sqlfather.table.mapper.TableInfoMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author mrzhu
* @description 针对表【table_info(表信息)】的数据库操作Service实现
* @createDate 2023-08-30 21:55:00
*/
@Service
public class TableInfoServiceImpl extends ServiceImpl<TableInfoMapper, TableInfo>
    implements TableInfoService{
    private final static Gson GSON = new Gson();
    
    /**
     * 校验表信息
     * @param tableInfo
     * @param add
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
        if (reviewStatus!=null && !ReviewStatusEnum.valid(reviewStatus)) {
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
}




