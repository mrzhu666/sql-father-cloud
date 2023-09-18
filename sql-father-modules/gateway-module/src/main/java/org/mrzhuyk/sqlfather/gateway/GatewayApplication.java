package org.mrzhuyk.sqlfather.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@EnableDiscoveryClient // 启动服务发现客户端
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
    
    /**
     * 定义一个简单的路由  添加了一个 ExceptionTestGatewayFilter filter
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder
            .routes()
            .route(p -> p
                .predicate(serverWebExchange -> serverWebExchange.getRequest().getURI().getPath().contains("/test"))
                .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(new ExceptionTestGatewayFilter()))
                .uri("https://localhost/"))
            .build();
    }
    
    /**
     * description:  定义一个Filter 只是简单的抛出异常
     * @return
     */
    private class ExceptionTestGatewayFilter implements GatewayFilter, Ordered {
        @Override
        public int getOrder() {
            return 1;
        }
        
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            //为了方便，简单抛出ResponseStatusException
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Test Exception Message");
        }
    }
    

    
}
