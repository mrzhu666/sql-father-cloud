# 基础镜像
FROM openjdk:8-jdk-alpine

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录，比如 target/yuoj-backend-user-service-0.0.1-SNAPSHOT.jar
ADD target/knife4j-module-1.0.0.jar .

# 暴露端口
EXPOSE 8499

# 启动命令
ENTRYPOINT ["java","-jar","/app/knife4j-module-1.0.0.jar","--spring.profiles.active=dev"]