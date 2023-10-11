package org.mrzhuyk.sqlfather.user.feign;

import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "sql-father-user-server")
public interface UserClient {
    
    /**
     * 获取当前登录用户
     * @return
     */
    @GetMapping("/user/get/login")
    UserVO getLoginUser();
    
}
