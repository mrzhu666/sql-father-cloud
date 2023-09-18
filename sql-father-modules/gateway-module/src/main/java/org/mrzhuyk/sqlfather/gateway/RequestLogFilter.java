package org.mrzhuyk.sqlfather.gateway;

import cn.hutool.core.util.ObjectUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.*;

/**
 * 过滤器产生日志
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "log.request.enabled", havingValue = "true", matchIfMissing = true)
public class RequestLogFilter implements GlobalFilter, Ordered {
    static final Gson gson = new Gson();
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        String url = getOriginalRequestUrl(exchange); // 请求url
        String method = request.getMethodValue();  //请求方法
        
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        boolean flag = StringUtils.startsWithIgnoreCase(header, MediaType.APPLICATION_JSON_VALUE); // 是否为json格式
        if (flag) {
            // 从缓存中读取body
            Object obj = exchange.getAttributes().get(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
            String jsonParam = null;
            if (ObjectUtil.isNotNull(obj)) {
                DataBuffer buffer = (DataBuffer) obj;
                CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
                jsonParam = charBuffer.toString();
            }
            log.info("{} {}, params: {}", method, url, jsonParam);
        } else {
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            String parameters = gson.toJson(queryParams);
            log.info("{} {}, params: {}", method, url, parameters);
        }
        return chain.filter(exchange);
    }
    
    /**
     * 获取原始请求路径
     */
    private String getOriginalRequestUrl(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        LinkedHashSet<URI> uris = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        URI requestUri = uris.stream().findFirst().orElse(request.getURI());
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        return UriComponentsBuilder.fromPath(requestUri.getRawPath()).queryParams(queryParams).build().toUriString();
    }
    
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
