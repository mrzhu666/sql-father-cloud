# 基础镜像
FROM openjdk:8-jdk-alpine

# 指定工作目录
WORKDIR /app

# 将 jar 包添加到工作目录，比如 target/yuoj-backend-user-service-0.0.1-SNAPSHOT.jar
ADD target/report-module-1.0.0.jar .

# 暴露端口
EXPOSE 8505

# 启动命令
ENTRYPOINT ["java", \
            "-Xmx256M", \
            "-Xms64M", \
            "-Xss256k", \
            "-Dnacos.common.processors=2", \
            "-Dnacos.remote.client.grpc.pool.core.size=3", \
            "-Dnacos.remote.client.grpc.pool.max.size=3", \
            "-jar","/app/report-module-1.0.0.jar", \
            "--spring.profiles.active=dev", \
            "--server.tomcat.threads.max=4"]