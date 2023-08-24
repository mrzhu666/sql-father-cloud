package org.mrzhuyk.sqlfather.spring.aop;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.mrzhuyk.sqlfather.core.annotation.AuthCheck;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.user.constant.UserConstant;
import org.mrzhuyk.sqlfather.user.po.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 根据注解权限校验
 */
@Aspect
@Component
@Slf4j
public class AuthInterceptor {
    
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        String mustRole = authCheck.mustRole();
        
        User user = getLoginUser();
        // 未登录
        if (user == null || user.getId() == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        String role = user.getUserRole();
        
        //任意权限
        if (CollectionUtils.isNotEmpty(anyRole) && !anyRole.contains(role)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        
        //必须权限
        if (StringUtils.isNotEmpty(mustRole) && !StringUtils.equals(mustRole,role)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        
        // 通过权限校验，放行
        return joinPoint.proceed();
    }
    
    /**
     * @return 获取当前登录的用户
     */
    public User getLoginUser() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return (User)request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
    }
}
