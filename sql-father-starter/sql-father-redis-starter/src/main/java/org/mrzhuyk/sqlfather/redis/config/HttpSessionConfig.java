package org.mrzhuyk.sqlfather.redis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * redis分布式存储session
 */
@Configuration
//设置session过期时间,默认是1800秒，设置该属性会覆盖配置文件设置的时间？
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 30 * 60)
public class HttpSessionConfig {
}