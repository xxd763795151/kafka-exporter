//Copyright 2021 Xiaodong Xu, 763795151@qq.com
//
//Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
package com.xuxd.kafka.exporter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:30:49
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

    private String bootServer;

    private int requestTimeoutMs;

    private String securityProtocol;

    private String saslMechanism;

    private String saslJaasConfig;

    private boolean enableAcl;
}
