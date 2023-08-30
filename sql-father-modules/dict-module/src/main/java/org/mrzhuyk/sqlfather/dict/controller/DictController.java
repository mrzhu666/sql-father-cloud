package org.mrzhuyk.sqlfather.dict.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.dict.dto.DictAddRequest;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.dict.service.DictService;
import org.mrzhuyk.sqlfather.sql.feign.UserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "词典服务")
@Slf4j
@RestController
@RequestMapping("/dict")
public class DictController {
    @Resource
    DictService dictService;
    
    @Resource
    UserClient userClient;
    
    /**
     * 创建词库
     * @param dictAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Result<Long> addDict(DictAddRequest dictAddRequest, HttpServletRequest request) {
        if (dictAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }

        Dict dict = new Dict();
        BeanUtils.copyProperties(dictAddRequest,dict);
        return null;
    }

}
