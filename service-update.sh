# 镜像服务更新
service=dict-module

docker compose -f docker-swarm.service.yml build
docker compose -f docker-swarm.service.yml push
docker service update --image notebook-wsl-tailscale:5000/sql-father-cloud-$service:latest sql-father-cloud_$service

