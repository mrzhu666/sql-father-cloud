# sql-father项目全局索引

本项目是sql-father，是一款可视化的低代码自动化提效工具

## 微服务模块

主模块 sql-father-modules，相对路径 [sql-father-modules](../sql-father-modules)，里面的所有模块都是微服务模块。

| 模块          | 责任        | 索引文件（相对路径）                      |
| ------------- | ----------- | ----------------------------------------- |
| dict-module   | 词典        | [dict-module](./index/dict-module.md)     |
| field-module  | 字段        | [field-module](./index/field-module.md)   |
|               |             |                                           |
| report-module | 审核举报    | [report-module](./index/report-module.md) |
| sql-module    | SQL代码生成 | [sql-module](./index/sql-module.md)       |
| table-module  | 表          | [table-module](./index/table-module.md)   |
| user-module   | 用户模组    | [user-module](./index/user-module.md)     |

功能性微服务：

| 模块           | 责任        | 索引文件（相对路径）                        |
| -------------- | ----------- | ------------------------------------------- |
| gateway-module | 网关        | [gateway-module](./index/gateway-module.md) |
| knife4j-module | 聚合API文档 | [knife4j-module](./index/knife4j-module.md) |

## 服务模块

主模块 sql-father-service，相对路径 [sql-father-service](../sql-father-service)，里面的所有模块都是对应微服务的service接口、数据操作等。



## 共享模块

主模块 sql-father-common，相对路径 [sql-father-common](../sql-father-common)，等。

| 模块          | 职责                                                     | 索引文件（相对路径） |
| ------------- | -------------------------------------------------------- | -------------------- |
| base-common   | 基础模块，包括常量、结果类、异常捕捉等。                 |                      |
| core-common   | 核心功能相关类，包括自定义注解、枚举、代码生成门面模式。 |                      |
| dict-common   | 词典相关数据对象                                         |                      |
| field-common  | 字段相关数据对象                                         |                      |
| report-common | 审核相关数据对象                                         |                      |
| sql-common    | 代码类相关相关数据对象                                   |                      |
| table-common  | 表相关数据对象                                           |                      |
| user-common   | 用户相关数据对象                                         |                      |

## starter模块

主模块 sql-father-starter，相对路径 [sql-father-starter](../sql-father-starter)，等。

| 模块                        | 职责                                             | 索引文件（相对路径） |
| --------------------------- | ------------------------------------------------ | -------------------- |
| sql-father-knife4j-starter  | knife4j文档配置类                                |                      |
| sql-father-mybatis-starter  | mybatis配置类                                    |                      |
| sql-father-nacos-starter    | nacos依赖                                        |                      |
| sql-father-rabbitmq-starter | rabbitmq依赖                                     |                      |
| sql-father-redis-starter    | redis相关配置类                                  |                      |
| sql-father-spring-starter   | spring通用配置，包括全局异常捕捉、远程调用配置等 |                      |

## 远程调用API接口

主模块 sql-father-api，相对路径 [sql-father-api](../sql-father-api)，等。













































