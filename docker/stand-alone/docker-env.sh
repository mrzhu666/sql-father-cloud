# 重构容器并运行。方便调试测试容器
file=docker-compose.env.yml
service=rabbitmq

# 重构容器
sudo docker compose -f $file build $service

docker stack deploy -c $file $service

# 启动容器
#sudo docker compose -f $file up -d $service
# 查看日志
#sudo docker compose -f $file logs $service -f

