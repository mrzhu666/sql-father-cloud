package org.mrzhuyk.sqlfather.sql.controller;


import io.swagger.annotations.Api;
import org.mrzhuyk.sqlfather.sql.feign.UserClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "sql生成服务")
@RestController
@RequestMapping("/sql")
public class SQLController {
    
    @Resource
    UserClient userClient;
    
    

}
