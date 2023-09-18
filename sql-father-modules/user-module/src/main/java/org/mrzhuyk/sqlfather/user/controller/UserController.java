package org.mrzhuyk.sqlfather.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.dto.UserLoginRequest;
import org.mrzhuyk.sqlfather.sql.dto.UserRegisterRequest;
import org.mrzhuyk.sqlfather.sql.po.User;
import org.mrzhuyk.sqlfather.user.service.UserService;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "用户服务")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    
    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 返回用户ID
     */
    @ApiOperation("注册用户")
    @PostMapping("/register")
    public Result<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String userName = userRegisterRequest.getUserName();
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        long result = userService.userRegister(userName, userAccount, userPassword, checkPassword, UserConstant.DEFAULT_ROLE);
        return Result.success(result);
    }
    
    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return 返回用户信息
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return Result.success(user);
    }
    
    
    /**
     * 用户注销
     * @param request
     * @return
     */
    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return Result.success(result);
    }
    
    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    @ApiOperation("获取当前登录用户")
    @GetMapping("/get/login")
    public Result<UserVO> getLoginUser(HttpServletRequest request) {
        log.info("用户接口：/get/login");
        User user = userService.getLoginUser(request);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }
    
    
    // region 增删改查，管理员权限
}
