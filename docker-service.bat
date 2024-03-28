::重构容器并运行。方便调试测试容器
set file=docker-compose.service.yml
set service=user-module

::模块单独重新打包
::使用call原因 https://blog.csdn.net/weixin_40195422/article/details/125621814
call mvn clean package -pl :%service% -am -amd -Dmaven.test.skip=true
::重构容器
docker-compose -f %file% build %service%
::启动容器
docker-compose -f %file% up -d %service%
::查看日志
::start开启新窗口，因为无法ctrl c中断
::cmd /K 防止运行错误自动关闭窗口
start cmd /K docker-compose -f %file% logs %service% -f

::查看gateway日志输出
::start cmd /K docker-compose -f docker-compose.service.yml logs gateway-module -f