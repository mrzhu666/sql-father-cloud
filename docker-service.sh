# 重构容器并运行。方便调试测试容器
file=docker-compose.service.yml
service=user-module

# 模块单独重新打包
mvn clean package -pl :$service -am -amd -Dmaven.test.skip=true
# 重构容器
sudo docker compose -f $file build $service
# 启动容器
sudo docker compose -f $file up -d $service
# 查看日志
sudo docker compose -f $file logs $service -f

# 查看gateway日志输出
# docker-compose -f docker-compose.service.yml logs gateway-module -f