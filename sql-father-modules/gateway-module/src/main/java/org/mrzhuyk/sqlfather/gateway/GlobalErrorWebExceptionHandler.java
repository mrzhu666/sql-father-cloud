package org.mrzhuyk.sqlfather.gateway;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;


/**
 * 网关自定义异常捕捉
 */
@Slf4j
@Order(-1)
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Handles global errors in the web exchange.
     *
     * @param exchange the server web exchange
     * @param ex the thrown exception
     * @return a Mono representing the completion of error handling
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        // Check if the response is already committed
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        ServerHttpRequest request = exchange.getRequest();
        // 请求路径、ip、方法
        String method = request.getMethodValue();
        String requestPath = request.getPath().pathWithinApplication().value();
        log.error("ERROR:{} method:{} path:{}", ex.getMessage(), method, requestPath);
        
        // Set response content type to JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        //设置返回编码
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                //writeValueAsBytes 组装错误响应结果
                return bufferFactory.wrap(
                    objectMapper.writeValueAsBytes(
                        Result.error(50000, "网关捕获到异常:" + ex.getMessage())));
            } catch (JsonProcessingException e) {
                log.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}