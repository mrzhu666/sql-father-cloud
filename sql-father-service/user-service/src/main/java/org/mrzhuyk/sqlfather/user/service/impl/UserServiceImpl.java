package org.mrzhuyk.sqlfather.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.po.User;
import org.mrzhuyk.sqlfather.user.service.UserService;
import org.mrzhuyk.sqlfather.user.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author mrzhu
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-08-11 20:51:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "mrzhuyk";
    
    /**
     * 用户注册
     *
     * @param userName 用户名
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @param userRole 用户角色
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userName, String userAccount, String userPassword, String checkPassword, String userRole) {
        // 校验
        if (StringUtils.isAnyBlank(userName, userAccount, userPassword, checkPassword, userRole)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "参数为空");
        }
        if (userName.length() > 16) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "用户名过长");
        }
        if (userAccount.length() < 4) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "用户密码过短");
        }
        synchronized (userAccount.intern()) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                // 重复账号
                throw new BizException(ErrorEnum.PARAMS_ERROR, "账号重复");
            }
            //密码加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            //插入数据
            User user = new User();
            user.setUserName(userName);
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserRole(userRole);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BizException(ErrorEnum.INTERNAL_SERVER_ERROR,"注册失败，数据库错误");
            }
            return user.getId();
        }
    }
    
    
    /**
     * 用户登录
     *
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "用户名过短");
        }
        if (userPassword.length() < 8) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "用户密码过短");
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.getOne(queryWrapper);
        
        if (user == null || user.getId() == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR,"用户不存在或密码错误");
        }
        //记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE,user);
        return user;
    }
    
    /**
     * 获取当前用户
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //先判断是否已经登录
        User currentUser = (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null || currentUser.getId() == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        
        // 为什么要重新查询用户
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        
        // 现在还有redis分布式session，在session存，相当于在redis了吧？
        
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
    
    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        return user!=null && UserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }
    
    /**
     * 用户注册
     * @param request
     * @return
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
            throw new BizException(ErrorEnum.OPERATION_ERROR, "未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }
}




