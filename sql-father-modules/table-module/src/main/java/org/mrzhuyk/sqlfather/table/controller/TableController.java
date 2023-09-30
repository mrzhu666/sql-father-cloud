package org.mrzhuyk.sqlfather.table.controller;


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
import org.mrzhuyk.sqlfather.field.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.table.dto.TableInfoAddRequest;
import org.mrzhuyk.sqlfather.table.dto.TableInfoQueryRequest;
import org.mrzhuyk.sqlfather.table.dto.TableInfoUpdateRequest;
import org.mrzhuyk.sqlfather.table.po.TableInfo;
import org.mrzhuyk.sqlfather.table.service.TableInfoService;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Api("表服务")
@RestController
@RequestMapping("/table_info")
public class TableController {
    @Resource
    private TableInfoService tableInfoService;
    
    @Resource
    private UserClient userClient;
    
    private final static Gson GSON = new Gson();
    
    // region 增删改查
    
    /**
     * 创建表
     * @param tableInfoAddRequest
     * @return
     */
    @ApiOperation("创建表请求")
    @PostMapping("/add")
    public Result<Long> addTableInfo(@RequestBody TableInfoAddRequest tableInfoAddRequest) {
        if (tableInfoAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoAddRequest, tableInfo);
        UserVO loginUser = userClient.getLoginUser();
        tableInfo.setUserId(loginUser.getId());
        tableInfoService.validAndHandleTableInfo(tableInfo, true);  //校验
        
        boolean save = tableInfoService.save(tableInfo);
        if (!save) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(tableInfo.getId());
    }
    /**
     * 删除
     *  仅限本人或管理员
     * @param deleteRequest
     * @return
     */
    @ApiOperation("删除表，仅限本人或管理员")
    @PostMapping("/delete")
    public Result<Boolean> deleteTableInfo(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId()<=0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        TableInfo tableInfo = tableInfoService.getById(deleteRequest.getId());
        if (tableInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        if (!tableInfo.getUserId().equals(loginUser.getId()) || loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean b = tableInfoService.removeById(tableInfo.getId());
        return Result.success(b);
    }
    
    /**
     * 更新（仅管理员）
     *  用于更新审核状态
     * @param tableInfoUpdateRequest
     * @return
     */
    @ApiOperation("更新（仅管理员）")
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public Result<Boolean> updateTableInfo(@RequestBody TableInfoUpdateRequest tableInfoUpdateRequest) {
        if (tableInfoUpdateRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        TableInfo tableInfo = new TableInfo();
        BeanUtils.copyProperties(tableInfoUpdateRequest, tableInfo);
        // 参数校验
        tableInfoService.validAndHandleTableInfo(tableInfo, false);
        // 判断是否存在
        TableInfo oldTableInfo = tableInfoService.getById(tableInfoUpdateRequest.getId());
        if (oldTableInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        boolean result = tableInfoService.updateById(tableInfo);
        return Result.success(result);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id获取")
    @GetMapping("/get")
    public Result<TableInfo> getTableInfoById(Long id) {
        if (id == null || id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        TableInfo byId = tableInfoService.getById(id);
        if (byId == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        return Result.success(byId);
    }
    
    /**
     * 获取列表（仅管理员可使用）
     *
     * @param tableInfoQueryRequest
     * @return
     */
    @ApiOperation("获取列表（仅管理员可使用）")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/list")
    public Result<List<TableInfo>> listTableInfo(TableInfoQueryRequest tableInfoQueryRequest) {
        List<TableInfo> list = tableInfoService.list(getQueryWrapper(tableInfoQueryRequest));
        return Result.success(list);
    }
    
    /**
     * 分页获取列表
     *
     * @param tableInfoQueryRequest
     * @return
     */
    @ApiOperation("分页获取列表")
    @GetMapping("/list/page")
    public Result<Page<TableInfo>> listTableInfoByPage(TableInfoQueryRequest tableInfoQueryRequest){
        if (tableInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        long current = tableInfoQueryRequest.getCurrent();
        long pageSize = tableInfoQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<TableInfo> queryWrapper = getQueryWrapper(tableInfoQueryRequest);
        Page<TableInfo> page = tableInfoService.page(new Page<>(current, pageSize), queryWrapper);
        return Result.success(page);
    }
    
    
    /**
     * 获取当前用户可选的全部资源列表（只返回 id 和名称）
     *
     * @param tableInfoQueryRequest
     * @return
     */
    @ApiOperation("获取当前用户可选的全部资源列表（只返回 id 和名称）")
    @GetMapping("/my/list")
    public Result<List<TableInfo>> listMyTableInfo(TableInfoQueryRequest tableInfoQueryRequest) {
        if (tableInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        Long userId = tableInfoQueryRequest.getUserId();
        if (!loginUser.getId().equals(userId)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        
        LambdaQueryWrapper<TableInfo> tableInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tableInfoLambdaQueryWrapper
            .eq(TableInfo::getUserId, userId)
            .or()
            .eq(TableInfo::getReviewStatus, ReviewStatusEnum.PASS.getValue());
        tableInfoLambdaQueryWrapper.select(TableInfo::getId, TableInfo::getName);
        List<TableInfo> list = tableInfoService.list(tableInfoLambdaQueryWrapper);
        return Result.success(list);
    }
    
    /**
     * 分页获取当前用户可选的资源列表
     *
     * @param tableInfoQueryRequest
     * @return
     */
    @ApiOperation("分页获取当前用户可选的资源列表")
    @GetMapping("/my/list/page")
    public Result<Page<TableInfo>> listMyTableInfoByPage(TableInfoQueryRequest tableInfoQueryRequest) {
        if (tableInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        long current = tableInfoQueryRequest.getCurrent();
        long pageSize = tableInfoQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<TableInfo> queryWrapper = getQueryWrapper(tableInfoQueryRequest);
        queryWrapper
            .eq(TableInfo::getUserId, loginUser.getId())
            .or()
            .eq(TableInfo::getReviewStatus, ReviewStatusEnum.PASS.getValue());
        Page<TableInfo> page = tableInfoService.page(new Page<>(current, pageSize), queryWrapper);
        return Result.success(page);
    }
    
    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param tableInfoQueryRequest
     * @return
     */
    @ApiOperation("分页获取当前用户创建的资源列表")
    @GetMapping("/my/add/list/page")
    public Result<Page<TableInfo>> listMyAddTableInfoByPage(TableInfoQueryRequest tableInfoQueryRequest) {
        if (tableInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        Long userId = tableInfoQueryRequest.getUserId();
        if (loginUser.getId().equals(userId)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        long current = tableInfoQueryRequest.getCurrent();
        long pageSize = tableInfoQueryRequest.getPageSize();
        if (pageSize > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        LambdaQueryWrapper<TableInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
            .eq(TableInfo::getUserId, userId);
        Page<TableInfo> page = tableInfoService.page(new Page<>(current,pageSize), queryWrapper);
        return Result.success(page);
    }
    // endregion
    
    /**
     * 生成创建表的 SQL
     *
     * @param id
     * @return
     */
    @ApiOperation("生成创建表的 SQL")
    @PostMapping("/generate/sql")
    public Result<String> generateCreateSql(@RequestBody Long id) {
        if (id == null || id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        TableInfo tableInfo = tableInfoService.getById(id);
        if (tableInfo == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        TableSchema tableSchema = GSON.fromJson(tableInfo.getContent(), TableSchema.class);
        SQLBuilder sqlBuilder = new SQLBuilder();
        return Result.success(sqlBuilder.buildCreateTableSql(tableSchema));
    }
    
    /**
     * 获取查询包装类
     *  name、content模糊搜索，字段排序
     * @param tableInfoQueryRequest
     * @return
     */
    private LambdaQueryWrapper<TableInfo> getQueryWrapper(TableInfoQueryRequest tableInfoQueryRequest) {
        if (tableInfoQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        TableInfo tableInfoQuery = new TableInfo();
        BeanUtils.copyProperties(tableInfoQueryRequest, tableInfoQuery);
        String sortField = tableInfoQueryRequest.getSortField();
        String sortOrder = tableInfoQueryRequest.getSortOrder();
        String name = tableInfoQuery.getName();
        String content = tableInfoQuery.getContent();
        // 模糊搜索
        tableInfoQuery.setName(null);
        tableInfoQuery.setContent(null);
        
        QueryWrapper<TableInfo> tableInfoQueryWrapper = new QueryWrapper<>(tableInfoQuery);
        tableInfoQueryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        LambdaQueryWrapper<TableInfo> lambda = tableInfoQueryWrapper.lambda();
        lambda.like(StringUtils.isNotBlank(name), TableInfo::getName, name);
        lambda.like(StringUtils.isNotBlank(content), TableInfo::getContent, content);
        
        return lambda;
    }
}
