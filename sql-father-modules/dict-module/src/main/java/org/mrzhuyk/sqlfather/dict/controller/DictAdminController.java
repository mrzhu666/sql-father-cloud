package org.mrzhuyk.sqlfather.dict.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.annotation.AuthCheck;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.dict.dto.DictQueryRequest;
import org.mrzhuyk.sqlfather.dict.dto.DictUpdateRequest;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.dict.service.DictService;
import org.mrzhuyk.sqlfather.rabbitmq.RabbitmqService;
import org.mrzhuyk.sqlfather.rabbitmq.config.RabbitmqConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "词库服务")
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictAdminController {
    
    private static final String REDIS_PREFIX_KEY = "dict:public:"; // 公开数据缓存
    
    @Resource
    private DictService dictService;
    
    @Resource
    RabbitmqService rabbitmqService;
    
    /**
     * 更新词库，管理员权限
     * @param dictUpdateRequest 请求更新的对象
     * @return 返回是否更新成功
     */
    @AuthCheck(mustRole = "admin")
    @ApiOperation("更新词库，管理员权限")
    @PostMapping("/update")
    public Result<Boolean> updateDict(@RequestBody DictUpdateRequest dictUpdateRequest) {
        if (dictUpdateRequest == null || dictUpdateRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictUpdateRequest,dict);
        // 参数校验
        dictService.validAndHandleDict(dict, false);
        long id = dictUpdateRequest.getId();
        // 判断是否存在
        Dict oldDict = dictService.getById(id);
        if (oldDict == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        boolean res = dictService.updateById(dict);
        redisClear(); // 清除缓存
        return Result.success(res);
    }
    
    /**
     * 获取列表，管理员权限
     * @param dictQueryRequest 根据name和content模糊查询，sortField排序
     * @return 返回词库实体列表
     */
    @ApiOperation("获取列表，管理员权限")
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public Result<List<Dict>> listDict(DictQueryRequest dictQueryRequest) {
        List<Dict> dictList = dictService.list(dictService.getQueryWrapper(dictQueryRequest));
        return Result.success(dictList);
    }
    
    
    
    @ApiOperation("清除缓存")
    @GetMapping("/redis/clear")
    public Result<Boolean> redis() {
        redisClear();
        return Result.success(true);
    }
    
    public void redisClear() {
        rabbitmqService.send(REDIS_PREFIX_KEY, RabbitmqConfig.TOPIC_EXCHANGE_NAME, RabbitmqConfig.ROUTING_KEY);
    }
}
