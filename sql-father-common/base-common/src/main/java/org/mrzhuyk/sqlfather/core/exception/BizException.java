package org.mrzhuyk.sqlfather.core.exception;

import lombok.Data;

/**
 * 业务异常类
 * exmaple:
 *  throw new BizException("500",“业务异常");
 */
@Data
public class BizException extends RuntimeException {
    protected int errorCode;
    protected String errorMsg;
    public BizException() {
        super();
    }
    
    public BizException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getResultMsg());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }
    
    public BizException(BaseErrorInfoInterface errorInfoInterface,String errorMsg) {
        super(errorInfoInterface.getResultMsg());
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorMsg;
    }
    
    public BizException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getResultMsg(), cause);
        this.errorCode = errorInfoInterface.getResultCode();
        this.errorMsg = errorInfoInterface.getResultMsg();
    }
    
    
    public BizException(int errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    
    public BizException(int errorCode, String errorMsg, Throwable cause) {
        super(errorMsg, cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    
    @Override
    public Throwable fillInStackTrace() {
        //return this; 直接返回this，会导致堆栈信息丢失
        return super.fillInStackTrace();
    }
    
    @Override
    public String toString() {
        return "BizException{" +
            "errorCode='" + errorCode + '\'' +
            ", errorMsg='" + errorMsg + '\'' +
            '}';
    }
}
