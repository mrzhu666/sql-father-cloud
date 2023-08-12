package org.mrzhuyk.sqlfather.core.exception;

/**
 *
 */
public interface BaseErrorInfoInterface {
    /** 错误码*/
    int getResultCode();
    /** 错误描述*/
    String getResultMsg();
}
