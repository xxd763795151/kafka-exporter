# kafka-exporter
kafka-exporter 采集kafka相关metric，可以集成prometheus进行监控告警。  
该kafka-exporter 目前只作为监控指标采集的补充。  
完整监控、告警搭建操作，查看：https://blog.csdn.net/x763795151/article/details/119705372

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
## 采集指标
* 消费积压信息  
* 消费位点（在grafana展示每个消费组消费的tps）
## 相关文件介绍
在工程的根目录下存在kafka_broker.yml、kafka_alert.yml...等配置文件
* kafka_broker.yml: jmx_exporter的配置文件  
* kafka_alert.yml: prometheus关于kafka的告警配置文件  
* grafana.json: grafana监控面板配置文件  