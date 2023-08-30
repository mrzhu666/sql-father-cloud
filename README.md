

此项目为sql-father-backend-public的微服务版本

原项目地址：[liyupi/sql-father-backend-public: 新项目：快速生成 SQL 和模拟数据的网站（Java 后端），大幅提高开发测试效率！by 程序员鱼皮 (github.com)](https://github.com/liyupi/sql-father-backend-public)

项目结构参考：[blog-aurora/aurora-blog: 🔥Aurora博客是一个基于Spring Cloud Alibaba的多人微服务博客项目，前台和后台界面非常漂亮，特征：邮箱链接验证、账户锁定等邮件功能。前端技术：TypeScript + Vue3 + Pinia + NaiveUi，后端技术：Spring Cloud Alibaba + RabbitMq + Seata + Oauth2。 (github.com)](https://github.com/blog-aurora/aurora-blog)



每个微服务启动类都要固定在org.mrzhuyk.sqlfather



父模块和依赖模块问题

父模块和依赖模块的依赖都会引入

groupID设置的问题

common,starter->service->module->

common->api



# 项目结构

```
auroraBackend
├── sql-father-common          // 各类服务的实体、常量、注解
│   ├── admin-common
│   ├── sql-father-core        // 包含核心生成方法
│   ├── file-common
│   └── message-common
├── aurora-modules            // 
│   ├── admin-boot
│   ├── article-boot
│   ├── auth-server-boot
│   ├── comment-boot
│   ├── file-boot
│   ├── gateway-boot
│   └── message-boot
├── aurora-starter                   // 各种组件配置类。其它配置？
│   ├── aurora-amqp-starter
│   ├── aurora-datasource-starter
│   ├── aurora-feign-starter
│   ├── aurora-mybatis-starter
│   ├── aurora-nacos-starter
│   ├── aurora-oauth2-starter
│   ├── aurora-redis-starter
│   ├── aurora-seata-starter
│   ├── aurora-security-starter
│   ├── aurora-sentinel-starter
│   ├── aurora-spring-boot-starter
│   └── aurora-swagger-starter
├── common-api                       // 微服务间的远程调用接口
│   ├── admin-api
│   ├── amqp-api
│   ├── amqp-mail-api
│   ├── article-api
│   ├── comment-api
│   ├── gateway-api
│   ├── mail-api
│   └── oauth-api
├── service                          // 业务逻辑，数据库交互
│   ├── admin-service
│   ├── article-service
│   ├── auth-server-service
│   ├── comment-service
│   ├── file-service
│   └── message-service
└── support
    └── support-service
```

# 模块

## 用户模块

用户注册、登录、注销、获取。





## SQL模块

根据表信息、字段信息、选择的词典生成对应的代码



sql-father-redis-starter：redis实现分布式session



利用工厂模式封装不同种类的生成算法。包括以下几种生成算法：

1. 



## 核心功能模块

原单体项目核心类为 GeneratorFacade，它根据表信息生成所有类型代码。里面另外有两个方法用于验证数据，可以剥离到其它类。主要是核心方法里某个数据模拟类，耦合词典服务，调用去查询词典数据库。

```java
public class DictDataGenerator implements DataGenerator {
    
    private static final DictService dictService = SpringContextUtils.getBean(DictService.class);

    private final static Gson GSON = new Gson();
}

```

为解耦服务，有两个方法一是该生成类做成微服务，提供接口给其它微服务调用。二是做成模块，导入到各自微服务调用。





修改接口添加一个默认方法，提供给词典生成方法。然后其它服务调用方法时，远程调用词典服务查询词典，然后传入改词典参数







```java
public interface DataGenerator {
    Dict dict = null;
    
    /**
     * 生成
     *
     * @param field 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(Field field, int rowNum);
    
    /**
     * 提供给词典生成方法使用，传入词典参数
     * @param dict
     */
    public default void setDict(Dict dict) {
    }
}

```



# 核心功能

## SQL代码生成

不同的SQL数据库的语法规则有略微差距，称之为sql方言，这里设计工厂模式生成不同的方言类，再采用双重校验锁设计工厂，可以保证方言类都是单例。



# 权限系统

## 



# 遇到的问题

1. 微服务的划分和细粒度
   - 采取根据表进行划分
   - 
2. 一些核心功能类调用service查询数据库



