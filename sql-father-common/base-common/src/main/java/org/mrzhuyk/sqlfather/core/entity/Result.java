package org.mrzhuyk.sqlfather.core.entity;

import lombok.Data;
import org.mrzhuyk.sqlfather.core.exception.BaseErrorInfoInterface;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;

/**
 * 返回数据，响应封装
 * @param <T> 响应数据，异常时为空
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    //业务执行成功
    public static <T> Result<T> success(T data){
        Result<T> result=new Result<>();
        result.setCode(ErrorEnum.SUCCESS.getResultCode());
        result.setMessage(ErrorEnum.SUCCESS.getResultMsg());
        result.setData(data);
        return result;
    }
    
    public static Result<String> error(int code,String message){
        Result<String> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
    
    public static Result<String> error(BaseErrorInfoInterface errorInfo) {
        return error(errorInfo.getResultCode(),errorInfo.getResultMsg());
    }
    
    public static Result<String> error(BaseErrorInfoInterface errorInfo,String msg) {
        return error(errorInfo.getResultCode(),msg);
    }
    
    public static Result<String> error(BizException biz) {
        return error(biz.getErrorCode(), biz.getErrorMsg());
    }
    
    //@Override
    //public String toString() {
    // return JSONObject.toJSONString(this);
    //}
}
