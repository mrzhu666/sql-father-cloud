package org.mrzhuyk.sqlfather.gateway;

import lombok.Data;

import java.util.Date;

@Data
public class GatewayLog {
    /**
     * 访问实例
     */
    private String targetServer;
    /**
     * 请求相对路径
     */
    private String requestPath;
    /**
     * 请求方法 :get post
     */
    private String requestMethod;
    /**
     * 请求协议:http rpc
     */
    private String schema;
    
    /**
     * 请求体
     */
    private String requestBody;
    /**
     * 响应体
     */
    private String responseBody;
    /**
     * 请求ip
     */
    private String ip;
    /**
     * 请求时间
     */
    private String requestTime;
    /**
     * 响应时间
     */
    private String responseTime;
    /**
     * 执行时间，单位毫秒
     */
    private long executeTime;
}
