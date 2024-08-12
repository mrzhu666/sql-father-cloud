package org.mrzhuyk.sqlfather.spring.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 远程调用时复制请求头
 */
@Slf4j
@Configuration
public class FeignConfig implements RequestInterceptor {
    @Bean
    public Decoder feignDecoder() {
        return new FeignResultDecoder();
    }
    
    /**
     * 复写feign请求对象
     * @param requestTemplate hhh
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取请求头
        Map<String,String> headers = getHeaders(Objects.requireNonNull(getHttpServletRequest()));
        //for (Map.Entry<String, String> stringStringEntry : headers.entrySet()) {
        //    String key = stringStringEntry.getKey();
        //    String value = stringStringEntry.getValue();
        //    requestTemplate.header(key, value);
        //}
        requestTemplate.header("cookie", headers.get("cookie"));
    }
    //获取请求对象
    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //拿到请求头信息
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
