package org.mrzhuyk.sqlfather.spring.exception;

import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常捕捉
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 捕获业务错误
     */
    @ExceptionHandler(value = BizException.class)
    public Result<String> bizExceptionHandler(BizException biz){
        log.error("业务异常："+biz); //日志记录，用于复查
        return Result.error(biz);
    }
    
    /**
     * 捕获其它所有异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest req, Exception e){
        log.error("服务内部异常：",e);
        return Result.error(ErrorEnum.INTERNAL_SERVER_ERROR,"服务器异常："+e.getMessage());
    }
   
}
