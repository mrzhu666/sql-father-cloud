package org.mrzhuyk.sqlfather.core.exception;

/**
 * 自定义异常接口
 */
public interface BaseErrorInfoInterface {
    /** 错误码*/
    int getResultCode();
    /** 错误描述*/
    String getResultMsg();
}
