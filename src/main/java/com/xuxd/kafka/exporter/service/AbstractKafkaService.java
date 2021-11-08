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
package com.xuxd.kafka.exporter.service;

import com.xuxd.kafka.exporter.config.KafkaConfig;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AbstractOptions;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-09-13 14:06:05
 **/
public abstract class AbstractKafkaService {


    public static final String INNER_CONSUMER = "__kafka-exporter-inner_consumer";

    private KafkaConfig kafkaConfig;

    public AbstractKafkaService(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    protected Properties getProperties() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootServer());
        if (kafkaConfig.isEnableAcl()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaConfig.getSecurityProtocol());
            props.put(SaslConfigs.SASL_MECHANISM, kafkaConfig.getSaslMechanism());
            props.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaConfig.getSaslJaasConfig());
        }

        return props;
    }

    protected  <T extends AbstractOptions> T timeoutMs(AbstractOptions<T> options) {
        options.timeoutMs(kafkaConfig.getRequestTimeoutMs());
        return (T) options;
    }
}
