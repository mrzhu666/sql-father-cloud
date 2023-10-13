# 重构容器并运行。方便调试测试容器
file=docker-compose.env.yml
service=mysql

# 重构容器
docker-compose -f $file build $service
# 启动容器
docker-compose -f $file up -d $service
# 查看日志
docker-compose -f $file logs $service -f

