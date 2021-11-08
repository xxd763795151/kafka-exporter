#!/bin/bash
#Copyright 2021 Xiaodong Xu, 763795151@qq.com
#
#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

# 设置jvm堆大小及栈大小，栈大小最少设置为256K，不要小于这个值，比如设置为128，太小了
JAVA_MEM_OPTS="-Xmx512m -Xms512m -Xmn256m -Xss256k"

SCRIPT_DIR=`dirname $0`
PROJECT_DIR="$SCRIPT_DIR/.."
CONF_FILE="$PROJECT_DIR/config/application.yml"
TARGET="$PROJECT_DIR/lib/kafka-exporter.jar"
# 日志目录，默认为当前工程目录下
# 这个是错误输出，如果启动命令有误，输出到这个文件，应用日志不会输出到error.out，应用日志输出到上面的rocketmq-reput.log中
ERROR_OUT="$PROJECT_DIR/error.out"
# 不要修改进程标记，作为进程属性关闭使用，如果要修改，请把stop.sh里的该属性的值保持一致
PROCESS_FLAG="kafka-exporter-process-flag:${PROJECT_DIR}"

JAVA_OPTS="$JAVA_OPTS $JAVA_MEM_OPTS"

nohup java -jar $JAVA_OPTS $TARGET --spring.config.location="$CONF_FILE" --logging.home="$PROJECT_DIR" $PROCESS_FLAG 1>/dev/null 2>$ERROR_OUT &

echo "Kafka-exporter Started!"