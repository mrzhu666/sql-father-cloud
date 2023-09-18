package org.mrzhuyk.sqlfather.field.feign;

import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("sql-father-field-server")
public interface FieldClient {
    @GetMapping("/api/field/get/schema/auto")
    Result<List<FieldInfo>> getFieldByAuto(@RequestParam String[] words);
}
