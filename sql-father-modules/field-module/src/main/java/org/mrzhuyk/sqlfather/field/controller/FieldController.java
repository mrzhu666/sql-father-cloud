package org.mrzhuyk.sqlfather.field.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.annotation.AuthCheck;
import org.mrzhuyk.sqlfather.core.constant.CommonConstant;
import org.mrzhuyk.sqlfather.core.dto.DeleteRequest;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.builder.SQLBuilder;
import org.mrzhuyk.sqlfather.field.dto.FieldInfoAddRequest;
import org.mrzhuyk.sqlfather.field.dto.FieldInfoQueryRequest;
import org.mrzhuyk.sqlfather.field.dto.FieldInfoUpdateRequest;
import org.mrzhuyk.sqlfather.field.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.field.service.FieldInfoService;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Api("字段服务")
@RestController
@RequestMapping("/field_info")
public class FieldController {
    @Resource
    FieldInfoService fieldInfoService;
    
    @Resource
    UserClient userClient;
    
    private final static Gson GSON = new Gson();
    
    // region 增删改查
    
    /**
     * 添加字段
     */
    @ApiOperation("添加字段")
    @PostMapping("/add")
    public Result<Long> addFieldInfo(@RequestBody FieldInfoAddRequest fieldInfoAddRequest) {
        if (fieldInfoAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoAddRequest, fieldInfo);
        // 校验
        fieldInfoService.validAndHandleFieldInfo(fieldInfo, true);
        UserVO loginUser = userClient.getLoginUser();
        fieldInfo.setUserId(loginUser.getId());
        boolean save = fieldInfoService.save(fieldInfo);
        if (!save) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(fieldInfo.getId());
    }
    
    @ApiOperation("批量添加")
    @PostMapping("/batch_add")
    public Result<Boolean> batchAddFieldInfo(@RequestBody List<TableSchema.Field> fieldList) {
        if (fieldList == null || fieldList.isEmpty()) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        boolean save = fieldInfoService.batchAddFieldInfo(fieldList);
        if (!save) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        
        return Result.success(true);
    }
    
    /**
     * 删除
     */
    @ApiOperation("管理员删除")
    @PostMapping("/delete")
    public Result<Boolean> deleteFieldInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        long id = deleteRequest.getId();
        // 判断是否存在
        FieldInfo oldFieldInfo = fieldInfoService.getById(id);
        if (oldFieldInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldFieldInfo.getUserId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean b = fieldInfoService.removeById(id);
        return Result.success(b);
    }
    
    /**
     * 更新（仅管理员）
     */
    @ApiOperation("更新字段，仅管理员")
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public Result<Boolean> updateFieldInfo(@RequestBody FieldInfoUpdateRequest fieldInfoUpdateRequest) {
        if (fieldInfoUpdateRequest == null || fieldInfoUpdateRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoUpdateRequest, fieldInfo);
        // 参数校验
        fieldInfoService.validAndHandleFieldInfo(fieldInfo, false);
        long id = fieldInfoUpdateRequest.getId();
        // 判断是否存在
        FieldInfo oldFieldInfo = fieldInfoService.getById(id);
        if (oldFieldInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        boolean result = fieldInfoService.updateById(fieldInfo);
        return Result.success(result);
    }
    
    /**
     * 根据 id 获取
     */
    @ApiOperation("获取字段")
    @GetMapping("/get")
    public Result<FieldInfo> getFieldInfoById(long id) {
        if (id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = fieldInfoService.getById(id);
        return Result.success(fieldInfo);
    }
    
    /**
     * 获取列表（仅管理员可使用）
     */
    @ApiOperation("获取列表（仅管理员可使用）")
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public Result<List<FieldInfo>> listFieldInfo(FieldInfoQueryRequest fieldInfoQueryRequest) {
        List<FieldInfo> fieldInfoList = fieldInfoService.list(getQueryWrapper(fieldInfoQueryRequest));
        return Result.success(fieldInfoList);
    }
    
    /**
     * 分页获取列表
     */
    @ApiOperation("分页获取列表")
    @GetMapping("/list/page")
    public Result<Page<FieldInfo>> listFieldInfoByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        if (fieldInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        long current = fieldInfoQueryRequest.getCurrent();
        long pageSize = fieldInfoQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        QueryWrapper<FieldInfo> queryWrapper = getQueryWrapper(fieldInfoQueryRequest);
        
        Page<FieldInfo> page = fieldInfoService.page(new Page<>(current, pageSize), queryWrapper);
        
        return Result.success(page);
    }
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     */
    @ApiOperation("获取当前用户可选的全部资源列表（只返回 id 和名称）")
    @GetMapping("/my/list")
    public Result<List<FieldInfo>> listMyFieldInfo(FieldInfoQueryRequest fieldInfoQueryRequest) {
        if (fieldInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null || loginUser.getId() <= 0) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        // 查询审核通过或个人的
        LambdaQueryWrapper<FieldInfo> fieldInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        fieldInfoLambdaQueryWrapper
            .eq(FieldInfo::getUserId, loginUser.getId())  // 个人
            .or()
            .eq(FieldInfo::getReviewStatus, ReviewStatusEnum.PASS.getValue()); // 审核通过
        fieldInfoLambdaQueryWrapper.select(FieldInfo::getId, FieldInfo::getName);  // 选择列
        List<FieldInfo> list = fieldInfoService.list(fieldInfoLambdaQueryWrapper);
        return Result.success(list);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     */
    @ApiOperation("分页获取当前用户可选的资源列表")
    @GetMapping("/my/list/page")
    public Result<Page<FieldInfo>> listMyFieldInfoByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        if (fieldInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        long current = fieldInfoQueryRequest.getCurrent();
        long pageSize = fieldInfoQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<FieldInfo> fieldInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询过审的或个人的
        fieldInfoLambdaQueryWrapper
            .eq(FieldInfo::getUserId, loginUser.getId())
            .or()
            .eq(FieldInfo::getReviewStatus, ReviewStatusEnum.PASS.getValue());
        Page<FieldInfo> page = fieldInfoService.page(new Page<>(current, pageSize), fieldInfoLambdaQueryWrapper);
        return Result.success(page);
    }
    
    
    /**
     * 分页获取当前用户创建的资源列表
     */
    @ApiOperation("分页获取当前用户创建的资源列表")
    @GetMapping("/my/add/list/page")
    public Result<Page<FieldInfo>> listMyAddFieldInfoByPage(FieldInfoQueryRequest fieldInfoQueryRequest) {
        if (fieldInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        fieldInfoQueryRequest.setUserId(loginUser.getId());
        long current = fieldInfoQueryRequest.getCurrent();
        long size = fieldInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Page<FieldInfo> fieldInfoPage = fieldInfoService.page(new Page<>(current, size),
            getQueryWrapper(fieldInfoQueryRequest));
        return Result.success(fieldInfoPage);
    }
    
    
    // endregion
    
    /**
     * 生成创建字段的 SQL
     */
    @ApiOperation("生成创建字段的 SQL")
    @PostMapping("/generate/sql")
    public Result<String> generateCreateSql(@RequestBody long id) {
        if (id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        FieldInfo fieldInfo = fieldInfoService.getById(id);
        if (fieldInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        TableSchema.Field field = GSON.fromJson(fieldInfo.getContent(), TableSchema.Field.class);
        SQLBuilder sqlBuilder = new SQLBuilder();
        return Result.success(sqlBuilder.buildCreateFieldSql(field));
    }
    
    
    /**
     * 获取查询包装类
     * 名称、字段名称、内容模糊搜索。字段排序
     */
    private QueryWrapper<FieldInfo> getQueryWrapper(FieldInfoQueryRequest fieldInfoQueryRequest) {
        if (fieldInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "请求参数为空");
        }
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtils.copyProperties(fieldInfoQueryRequest, fieldInfo);
        
        String name = fieldInfo.getName();
        String fieldName = fieldInfo.getFieldName();
        String content = fieldInfo.getContent();
        
        String searchName = fieldInfoQueryRequest.getSearchName();
        String sortField = fieldInfoQueryRequest.getSortField();
        String sortOrder = fieldInfoQueryRequest.getSortOrder();
        
        // name、fieldName、content 需支持模糊搜索
        fieldInfo.setName(null);
        fieldInfo.setFieldName(null);
        fieldInfo.setContent(null);
        
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>(fieldInfo);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(fieldName), "fieldName", fieldName);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        
        // 同时按 name、fieldName 搜索
        if (StringUtils.isNotBlank(searchName)) {
            queryWrapper.like("name", searchName).or().like("fieldName", searchName);
        }
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
            sortField);
        return queryWrapper;
    }
    
    /**
     * 根据提供的词语模糊查询字段信息
     */
    @ApiOperation("模糊查询字段信息")
    @GetMapping("/get/schema/auto")
    public Result<List<FieldInfo>> getFieldByAuto(@RequestParam("words") String[] words) {
        // 根据单词去词库里匹配列信息，未匹配到的使用默认值
        QueryWrapper<FieldInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("name", Arrays.asList(words)).or().in("fieldName", Arrays.asList(words));
        List<FieldInfo> list = fieldInfoService.list(queryWrapper);
        return Result.success(list);
    }
    
    
}
