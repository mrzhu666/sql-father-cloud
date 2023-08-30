package org.mrzhuyk.sqlfather.sql.feign;

import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.sql.dto.UserLoginRequest;
import org.mrzhuyk.sqlfather.sql.dto.UserRegisterRequest;
import org.mrzhuyk.sqlfather.sql.po.User;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

@FeignClient("sql-father-user-server")
public interface UserClient {
    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 返回用户ID
     */
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest);
    
    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return 返回用户信息
     */
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request);
    
    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request);
    
    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public Result<UserVO> getLoginUser(HttpServletRequest request);
    
}
