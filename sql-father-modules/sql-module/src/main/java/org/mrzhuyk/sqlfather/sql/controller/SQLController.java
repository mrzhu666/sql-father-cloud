package org.mrzhuyk.sqlfather.sql.controller;


import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.GeneratorFacade;
import org.mrzhuyk.sqlfather.core.generator.builder.TableSchemaBuilder;
import org.mrzhuyk.sqlfather.dict.feign.DictClient;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.field.feign.FieldClient;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.sql.dto.GenerateByAutoRequest;
import org.mrzhuyk.sqlfather.sql.dto.GenerateBySqlRequest;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.GenerateVO;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(tags = "sql生成服务")
@RestController
@Slf4j
@RequestMapping("/sql")
public class SQLController {
    
    @Resource
    UserClient userClient;
    
    @Resource
    FieldClient fieldClient;
    
    @Resource
    DictClient dictClient;
    
    /**
     * 由表概要生成所有内容
     *
     * @param tableSchema
     * @return
     */
    @ApiOperation("表所有内容生成")
    @PostMapping("/generate/schema")
    public Result<GenerateVO> generateBySchema(@RequestBody TableSchema tableSchema) {
        // 将字段模拟类型为词典的，预处理mockParams为词典内容
        for (TableSchema.Field field : tableSchema.getFieldList()) {
            String mockType = field.getMockType();
            if (MockTypeEnum.DICT.getValue().equals(mockType)) {
                String mockParams = field.getMockParams();
                Dict dictById = dictClient.getDictById(Long.parseLong(mockParams));
                field.setMockParams(dictById.getContent());
            }
        }
        return Result.success(GeneratorFacade.generateAll(tableSchema));
    }
    
    /**
     * 智能填充
     *  根据输入的名称，自动导入可能的字段，即自动填充字段
     *  字段服务传入参数
     * @param autoRequest
     * @return
     */
    @ApiOperation("智能填充")
    @PostMapping("/get/schema/auto")
    public Result<TableSchema> getSchemaByAuto(@RequestBody GenerateByAutoRequest autoRequest) {
        if (autoRequest == null || StringUtils.isBlank(autoRequest.getContent())) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String content = autoRequest.getContent();
        String[] words = content.split("[,，]");
        if (ArrayUtils.isEmpty(words) || words.length > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        // 远程调用获取字段
        List<FieldInfo> fieldByAuto = fieldClient.getFieldByAuto(words);
        
        return Result.success(TableSchemaBuilder.buildFromAuto(words, fieldByAuto));
    }
    
    /**
     * 根据sql语句生成表概要
     * @param sqlRequest
     * @return
     */
    @ApiOperation("根据sql语句生成表概要")
    @PostMapping("/get/schema/sql")
    public Result<TableSchema> getSchemaBySQL(@RequestBody GenerateBySqlRequest sqlRequest) {
        if (sqlRequest == null || StringUtils.isBlank(sqlRequest.getSql())) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        return Result.success(TableSchemaBuilder.buildFromSql(sqlRequest.getSql()));
    }
    
    
    /**
     * 根据excel生成表概要
     * @param file
     * @return
     */
    @ApiOperation("根据excel生成表概要")
    @PostMapping("/get/schema/excel")
    public Result<TableSchema> getSchemaByExcel(MultipartFile file) {
        return Result.success(TableSchemaBuilder.buildFromExcel(file));
    }
    
    
    /**
     * 下载模拟数据excel
     * @param generateVO
     * @param response
     */
    @ApiOperation("下载模拟数据excel")
    @PostMapping("download/data/excel")
    public void downloadDataExcel(@RequestBody GenerateVO generateVO, HttpServletResponse response) {
        TableSchema tableSchema = generateVO.getTableSchema();
        String tableName = tableSchema.getTableName();
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里 URLEncoder.encode 可以防止中文乱码
            String fileName = URLEncoder.encode(tableName + "表数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            // 设置表头
            List<List<String>> headList = new ArrayList<>();
            for (TableSchema.Field field : tableSchema.getFieldList()) {
                List<String> head = Collections.singletonList(field.getFieldName());
                headList.add(head);
            }
            List<String> fieldNameList = tableSchema.getFieldList().stream().map(TableSchema.Field::getFieldName).collect(Collectors.toList());
            // 设置数据
            List<List<Object>> dataList = new ArrayList<>();
            for (Map<String, Object> data : generateVO.getDataList()) {
                List<Object> dataRow = fieldNameList.stream().map(data::get).collect(Collectors.toList());
                dataList.add(dataRow);
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream())
                .autoCloseStream(Boolean.FALSE)
                .head(headList)
                .sheet(tableName + "表")
                .doWrite(dataList);
        } catch (Exception e) {
            // 重置response
            response.reset();
            throw new BizException(ErrorEnum.INTERNAL_SERVER_ERROR, "下载失败");
        }
    }
    
    
    @ApiOperation("测试用接口")
    @GetMapping("/test")
    public Result<UserVO> test() {
        UserVO user = userClient.getLoginUser();
        return Result.success(user);
    }

    
}
