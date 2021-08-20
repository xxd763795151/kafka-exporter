# kafka-exporter
kafka-exporter 采集kafka相关metric，可以集成prometheus进行监控告警

# 使用
### 打包
```sh package.sh```
### 启动
```sh bin/start.sh```
### 停止
```sh bin/shutdown.sh```
### 配置文件
```config/application.yml```  
需要修改配置文件中kafka的地址：  
```
kafka:
  boot-server: localhost:9092
```  

### 访问
http://localhost:9097/prometheus

# 说明
目前只采集了消息积压信息，其它指标监控配置以及该exporter的具体使用看这里：https://blog.csdn.net/x763795151/article/details/119705372
  
因为从broker的jmx无法拿到积压信息，所以单写了一个exporter，如有其它指标，后续再补充