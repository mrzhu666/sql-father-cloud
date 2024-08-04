package org.mrzhuyk.sqlfather.dict.feign;

import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("sql-father-dict-server")
public interface DictClient {
    @GetMapping("/dict/get")
    Dict getDictById(@RequestParam("id") Long id);
}

