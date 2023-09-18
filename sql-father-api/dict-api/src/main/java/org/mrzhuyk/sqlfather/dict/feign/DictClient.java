package org.mrzhuyk.sqlfather.dict.feign;

import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("sql-father-dict-server")
public interface DictClient {
    @GetMapping("/api/dict/get")
    Dict getDictById(Long id);
}

