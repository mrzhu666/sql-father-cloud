package org.mrzhuyk.sqlfather.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.annotation.AuthCheck;
import org.mrzhuyk.sqlfather.core.dto.DeleteRequest;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.dto.UserAddRequest;
import org.mrzhuyk.sqlfather.sql.dto.UserQueryRequest;
import org.mrzhuyk.sqlfather.sql.dto.UserUpdateRequest;
import org.mrzhuyk.sqlfather.sql.po.User;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Api("用户服务（管理员）")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserAdminController {
    @Resource
    UserService userService;
    
    /**
     * 创建用户
     */
    @ApiOperation("创建用户（管理员）")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(user.getId());
    }
    
    /**
     * 删除用户
     */
    @ApiOperation("删除用户（管理员）")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() < 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        boolean b = userService.removeById(id);
        if (!b) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(true);
    }
    
    /**
     * 更新用户
     */
    @ApiOperation("更新用户（管理员）")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        boolean b = userService.updateById(user);
        if (!b) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        return Result.success(true);
    }
    
    /**
     * 根据 id 获取用户
     */
    @ApiOperation("根据id获取用户（管理员）")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<UserVO> getUserById(Integer id) {
        if (id == null || id < 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        User byId = userService.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(byId,userVO);
        return Result.success(userVO);
    }
    
    /**
     * 获取用户列表
     */
    @ApiOperation("获取用户列表（管理员）")
    @GetMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<List<UserVO>> listUser(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        User userQuery = new User();
        BeanUtils.copyProperties(userQueryRequest,userQuery);
        
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>(userQuery);
        List<User> list = userService.list(userQueryWrapper);
        
        List<UserVO> collect = list.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        
        return Result.success(collect);
    }
    
    /**
     * 分页获取用户列表
     * @param userQueryRequest
     * @return
     */
    @ApiOperation("分页获取用户列表（管理员）")
    @GetMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Result<Page<UserVO>> listUserByPage(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize));
        
        List<User> userList = userPage.getRecords();
        Page<UserVO> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> userVOList = userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        
        return Result.success(userVOPage);
    }
}
