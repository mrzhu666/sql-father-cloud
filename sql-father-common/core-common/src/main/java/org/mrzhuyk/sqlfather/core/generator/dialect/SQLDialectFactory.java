package org.mrzhuyk.sqlfather.core.generator.dialect;

import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方言采用工厂+策略模式
 * 单例采用了双重校验锁，其实可以直接采用饿汉式的
 * 这里偷懒了，系统没有定义具体使用哪种数据库
 */
public class SQLDialectFactory {
    private static final Map<String, SQLDialect> DIALECT_MAP = new ConcurrentHashMap<>();
    
    private SQLDialectFactory() {
    
    }
    
    /**
     * 获取方言实例
     * @param className 类名
     * @return
     */
    public static SQLDialect getDialect(String className) {
        SQLDialect dialect = DIALECT_MAP.get(className);
        if (null == dialect) {
            synchronized (className.intern()) {
                dialect = DIALECT_MAP.computeIfAbsent(className,
                    key->{
                        try {
                            return (SQLDialect) Class.forName(className).newInstance();
                        } catch (Exception e) {
                            throw new BizException(ErrorEnum.INTERNAL_SERVER_ERROR);
                        }
                    });
            }
        }
        return dialect;
    }
}
