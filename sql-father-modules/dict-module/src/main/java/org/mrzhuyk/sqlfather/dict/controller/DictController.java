package org.mrzhuyk.sqlfather.dict.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.dto.DeleteRequest;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.enums.MockTypeEnum;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.core.generator.GeneratorFacade;
import org.mrzhuyk.sqlfather.dict.dto.DictAddRequest;
import org.mrzhuyk.sqlfather.dict.dto.DictQueryRequest;
import org.mrzhuyk.sqlfather.dict.enums.ReviewStatusEnum;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.dict.service.DictService;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.mrzhuyk.sqlfather.sql.vo.GenerateVO;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Api(tags = "词库服务")
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictController {
    private static final String REDIS_PREFIX_KEY = "dict:public:"; // 公开数据缓存
    

    private static final SecureRandom random = new SecureRandom();
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    
    @Resource
    DictService dictService;
    
    @Resource
    UserClient userClient;
    
    /**
     * 添加词库
     * @param dictAddRequest 添加的实体
     * @return 返回id
     */
    @ApiOperation("添加词库")
    @PostMapping("/add")
    public Result<Long> addDict(@RequestBody DictAddRequest dictAddRequest) {
        if (dictAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictAddRequest, dict);
        // 校验
        dictService.validAndHandleDict(dict, true);
        UserVO loginUser = userClient.getLoginUser();
        dict.setUserId(loginUser.getId());
        boolean result = dictService.save(dict);
        if (!result) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(dict.getId());
    }
    
    /**
     * 根据id获取词库数据
     * @param id 词库数据的id
     * @return 返回词库对象
     */
    @ApiOperation("根据id获取词库")
    @GetMapping("/get")
    public Result<Dict> getDictById(@RequestParam("id") Long id) {
        if (id == null || id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Dict byId = dictService.getById(id);
        return Result.success(byId);
    }
    
    
    /**
     * 删除词库
     * @param deleteRequest 包含删除词库的id
     * @return 返回是否成功删除
     */
    @ApiOperation("删除词库")
    @PostMapping("/delete")
    public Result<Boolean> deleteDict(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO userVO = userClient.getLoginUser();
        // 判断登录
        if (userVO == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        Long dictId = deleteRequest.getId();
        Dict dict = dictService.getById(dictId);
        // 判断数据存在
        if (dict == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!dict.getUserId().equals(userVO.getId()) && !userVO.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean b = dictService.removeById(dictId);
        return Result.success(b);
    }
    
    
    /**
     * 获取列表分页，公开词库
     * @param dictQueryRequest 带分页的查询
     * @return 返回词库分页
     */
    @ApiOperation("获取列表分页")
    @GetMapping("/list/page")
    public Result<Page<Dict>> listDictPage( DictQueryRequest dictQueryRequest) {
        long current = dictQueryRequest.getCurrent(); // 当前页
        long size = dictQueryRequest.getPageSize(); // 大小
        // 限制爬虫
        if (size>20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        // 走缓存
        String key = REDIS_PREFIX_KEY + StringUtils.joinWith(":","list",current,size);
        @SuppressWarnings("unchecked")
        Page<Dict> dictPage=(Page<Dict>)redisTemplate.opsForValue().get(key);
        if (dictPage==null) {
            dictPage = dictService.page(new Page<>(current, size),
                dictService.getQueryWrapper(dictQueryRequest));
            redisTemplate.opsForValue().set(key,dictPage,600+ random.nextInt(100), TimeUnit.SECONDS);
        }
        

        //Page<Dict> dictPage = dictService.page(new Page<>(current, size), getQueryWrapper(dictQueryRequest));
        return Result.success(dictPage);
    }
    

    
    
    /**
     * 获取当前用户可选的列表（只返回 id 和名称）
     *  公开的词库和个人词库
     * @param dictQueryRequest 查询实体，只用到用户id
     * @return 返回词库实体列表
     */
    @ApiOperation("获取当前用户可选的列表（只返回 id 和名称）")
    @GetMapping("/my/list")
    public Result<List<Dict>> listMyDict(DictQueryRequest dictQueryRequest) {
        if (dictQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        // 选择返回的字段
        final String[] fields = new String[]{"id", "name"};
        // 先查询通过审核的(公开)
        dictQueryRequest.setReviewStatus(ReviewStatusEnum.PASS.getValue());
        QueryWrapper<Dict> queryWrapper = dictService.getQueryWrapper(dictQueryRequest);
        queryWrapper.select(fields);
        List<Dict> dictList = dictService.list(queryWrapper);
        
        try {
            //再查询本人的
            UserVO loginUserVo = userClient.getLoginUser();
            Dict dictQuery = new Dict();
            dictQuery.setReviewStatus(null);
            dictQuery.setUserId(loginUserVo.getId());
            queryWrapper=new QueryWrapper<>(dictQuery);
            queryWrapper.select(fields);
            dictList.addAll(dictService.list(queryWrapper));
        } catch (Exception e) {
            // 未登录
        }
        // 根据 id 去重
        List<Dict> resultList = dictList.stream().collect(Collectors.collectingAndThen(
            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Dict::getId))), ArrayList::new));
        return Result.success(resultList);
    }
    
    /**
     * 获取当前用户可选的列表分页
     *  公开的词库和个人词库
     * @param dictQueryRequest 查询实体，只用到用户id
     * @return 返回词库实体分页
     */
    @ApiOperation("获取当前用户可选的列表分页")
    @GetMapping("/my/list/page")
    public Result<Page<Dict>> listMyDictPage(DictQueryRequest dictQueryRequest) {
        UserVO loginUserVo = userClient.getLoginUser();
        if (loginUserVo == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        long current = dictQueryRequest.getCurrent();
        long size = dictQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        // 获取用户的和已经过审的
        QueryWrapper<Dict> queryWrapper = dictService.getQueryWrapper(dictQueryRequest);
        queryWrapper.eq("userId", loginUserVo.getId())
            .or()
            .eq("reviewStatus", ReviewStatusEnum.PASS.getValue());
        Page<Dict> dictPage = dictService.page(new Page<>(current, size), queryWrapper);
        return Result.success(dictPage);
    }
    
    /**
     * 分页获取当前用户创建的资源列表
     * @param dictQueryRequest 使用到用户id
     * @return 返回词库实体分页
     */
    @ApiOperation("分页获取当前用户创建的资源列表")
    @GetMapping("/my/add/list/page")
    public Result<Page<Dict>> listMyAddDictByPage(DictQueryRequest dictQueryRequest) {
        if (dictQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        dictQueryRequest.setUserId(loginUser.getId());
        long current = dictQueryRequest.getCurrent();
        long size = dictQueryRequest.getPageSize();
        // 限制爬虫
        if (size > 20) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Page<Dict> dictPage = dictService.page(new Page<>(current, size),
            dictService.getQueryWrapper(dictQueryRequest));
        return Result.success(dictPage);
    }
    
    /**
     * 根据词库创建表，根据表返回所有生成内容
     * @param id 词库id
     * @return 返回生成内容实体
     */
    @ApiOperation("词库创建表，返回所有生成内容")
    @PostMapping("/generate/sql")
    public Result<GenerateVO> generateCreateSql(@RequestBody long id) {
        if (id < 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Dict dict = dictService.getById(id);
        if (dict == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 根据词库生成 Schema
        TableSchema tableSchema = new TableSchema();
        String name = dict.getName();
        tableSchema.setTableName("dict");
        tableSchema.setTableComment(name);
        
        TableSchema.Field idField = new TableSchema.Field();
        idField.setFieldName("id");
        idField.setFieldType("bigint");
        idField.setNotNull(true);
        idField.setComment("id");
        idField.setPrimaryKey(true);
        idField.setAutoIncrement(true);
        idField.setMockType(MockTypeEnum.INCREASE.getValue());
        
        TableSchema.Field dataField = new TableSchema.Field();
        dataField.setFieldName("data");
        dataField.setFieldType("text");
        dataField.setComment("数据");
        dataField.setMockType(MockTypeEnum.DICT.getValue());
        //dataField.setMockParams(String.valueOf(id));
        dataField.setMockParams(dict.getContent());
        
        List<TableSchema.Field> fieldList = new ArrayList<>();
        fieldList.add(idField);
        fieldList.add(dataField);
        tableSchema.setFieldList(fieldList);
        
        return Result.success(GeneratorFacade.generateAll(tableSchema));
    }
    
    
    
}
