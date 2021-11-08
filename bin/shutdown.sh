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

SCRIPT_DIR=`dirname $0`
PROJECT_DIR="$SCRIPT_DIR/.."
# 不要修改进程标记，作为进程属性关闭使用
PROCESS_FLAG="kafka-exporter-process-flag:${PROJECT_DIR}"
pkill -f $PROCESS_FLAG
echo 'Stop Kafka-exporter!'