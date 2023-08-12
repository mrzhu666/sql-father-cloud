package org.mrzhuyk.sqlfather.core.exception;


/**
 * 枚举通用的业务状态码
 */
public enum ErrorEnum implements BaseErrorInfoInterface {
    SUCCESS(0, "成功!"),
    PARAMS_ERROR(40000,"请求参数错误!"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    INTERNAL_SERVER_ERROR(50000, "服务器内部错误!"),
    OPERATION_ERROR(50001,"服务器正忙，请稍后再试!")
    ;
    private int resultCode;
    private String resultMsg;
    ErrorEnum(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }
    
    @Override
    public int getResultCode() {
        return resultCode;
    }
    
    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}