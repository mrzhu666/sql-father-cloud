package org.mrzhuyk.sqlfather.gateway;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 日志过滤器，用于记录日志
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered{
    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();
    
    static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayLog gatewayLog = new GatewayLog();
        ServerHttpRequest request = exchange.getRequest();
        // 请求路径、ip、协议、方法
        String requestPath = request.getPath().pathWithinApplication().value();
        String clientIp = request.getRemoteAddress().getHostString();
        String scheme = request.getURI().getScheme();
        String method = request.getMethodValue();
        gatewayLog.setRequestPath(requestPath);
        gatewayLog.setIp(clientIp);
        gatewayLog.setSchema(scheme);
        gatewayLog.setRequestMethod(method);
        gatewayLog.setRequestTime(simpleDateFormat.format(new Date().getTime()));
        
        MediaType contentType = request.getHeaders().getContentType();
        // 判断是否为json类型
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType) || MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
            return writeBodyLog(exchange, chain, gatewayLog);
        } else {
            return writeBasicLog(exchange, chain, gatewayLog);
        }
    }
    
    /**
     * 其它类型记录
     */
    private Mono<Void> writeBasicLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog) {
        //StringBuilder builder = new StringBuilder();
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        //for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
        //    builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ",")).append(" ");
        //}
        String params = queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + StringUtils.join(entry.getValue(), ",")).collect(Collectors.joining("&"));
        // 记录请求内容
        gatewayLog.setRequestBody(params);
        
        // 获取响应体
        ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);
        return chain.filter(exchange.mutate().response(decoratedResponse).build())
            .then(Mono.fromRunnable(() -> {
                //打印日志
                writeAccessLog(gatewayLog);
            }));
    }
    
    /**
     * 解决 request body 只能读取一次问题
     */
    private Mono<Void> writeBodyLog(ServerWebExchange exchange, GatewayFilterChain chain, GatewayLog gatewayLog) {
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
            .flatMap(body -> {
                gatewayLog.setRequestBody(body);
                return Mono.just(body);
            });
        
        // 通过 BodyInsert 插入 body(支持修改body), 避免 request body 只能获取一次
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
            .then(Mono.defer(() -> {
                // 重新封装请求
                ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);
                
                // 记录响应日志
                ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);
                
                // 记录普通的
                return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                    .then(Mono.fromRunnable(() -> {
                        // 打印日志
                        writeAccessLog(gatewayLog);
                    }));
            }));
    }
    
    /**
     * 打印日志并将日志内容写入mongodb
     */
    private void writeAccessLog(GatewayLog gatewayLog) {
        String requestPath = gatewayLog.getRequestPath();
        // 文档请求不打印
        if (requestPath.contains("doc")) {
            return;
        }
        
        log.info("{} {}, RequestBody:{} , ResponseBody:{}", gatewayLog.getRequestMethod(), gatewayLog.getRequestPath(), gatewayLog.getRequestBody(),gatewayLog.getResponseBody());
        // 写入mongodb
        //accessLogService.saveAccessLog(gatewayLog);
    }
    
    /**
     * 记录响应日志
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, GatewayLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        return new ServerHttpResponseDecorator(response) {
            @SneakyThrows
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    String responseTime = simpleDateFormat.format(new Date().getTime());
                    gatewayLog.setResponseTime(responseTime);
                    // 计算执行时间
                    long executeTime = (simpleDateFormat.parse(responseTime).getTime() - simpleDateFormat.parse(gatewayLog.getRequestTime()).getTime());
                    gatewayLog.setExecuteTime(executeTime);
                    
                    // 获取响应类型，如果是 json 就打印
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    
                    if (ObjectUtils.equals(this.getStatusCode(), HttpStatus.OK)
                        && StringUtils.isNotBlank(originalResponseContentType)
                        && originalResponseContentType.contains("application/json")) {
                        
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            
                            // 合并多个流集合，解决返回体分段传输
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            
                            // 释放掉内存
                            DataBufferUtils.release(join);
                            String responseResult = new String(content, StandardCharsets.UTF_8);
                            gatewayLog.setResponseBody(responseResult);
                            
                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                return super.writeWith(body);
            }
        };
        
    }
    
    /**
     * 请求装饰器，重新计算 headers
     */
    private ServerHttpRequest requestDecorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }
            
            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }
    
    
    /**
     * 顺序必须是<-1，否则标准的NettyWriteResponseFilter将在您的过滤器得到一个被调用的机会之前发送响应
     * 也就是说如果不小于 -1 ，将不会执行获取后端响应的逻辑
     */
    @Override
    public int getOrder() {
        return -100;
    }
}