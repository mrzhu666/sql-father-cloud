package org.mrzhuyk.sqlfather.spring.exception;

import io.seata.core.context.RootContext;
import io.seata.core.exception.TransactionException;
import io.seata.tm.api.GlobalTransactionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
    public Result<String> bizExceptionHandler(BizException biz) {
        log.error("业务异常：{}", biz.getErrorMsg()); //日志记录，用于复查
        // 获取抛出异常的堆栈信息
        StackTraceElement[] stackTrace = biz.getStackTrace();
        if (stackTrace.length > 0) {
            StackTraceElement element = stackTrace[0];
            log.error("异常发生在类：{}，方法：{}，行号：{}",
                element.getClassName(),
                element.getMethodName(),
                element.getLineNumber());
        }
        
        // 打印完整堆栈跟踪
        biz.printStackTrace();
        
        transactionRollback();
        return Result.error(biz);
    }
    
    
    /**
     * 捕获其它所有异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(Exception e)  {
        log.error("服务内部异常：",e);
        transactionRollback();
        return Result.error(ErrorEnum.INTERNAL_SERVER_ERROR,"服务器异常："+e.getMessage());
    }
    
    /**
     * 分布式事务回滚
     */
    public void transactionRollback() {
        if (StringUtils.isNotBlank(RootContext.getXID())){
            log.error("分布式事务回滚：{}", RootContext.getXID());
            try {
                GlobalTransactionContext.reload(RootContext.getXID()).rollback();
            } catch (TransactionException ex) {
                log.error("事务回滚异常：{}", ex.getMessage());
            }
        }
    }
    
}
