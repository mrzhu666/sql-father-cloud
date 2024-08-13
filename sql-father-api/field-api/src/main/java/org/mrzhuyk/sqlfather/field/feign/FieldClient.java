package org.mrzhuyk.sqlfather.field.feign;

import org.mrzhuyk.sqlfather.field.po.FieldInfo;
import org.mrzhuyk.sqlfather.sql.schema.TableSchema;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("sql-father-field-server")
public interface FieldClient {
    @GetMapping("/field_info/get/schema/auto")
    List<FieldInfo> getFieldByAuto(@RequestParam("words") String[] words);
    
    
    @PostMapping("/field_info/batch_add")
    Boolean batchAddFieldInfo(@RequestBody List<TableSchema.Field> fieldList);
}
