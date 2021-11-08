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
package com.xuxd.kafka.exporter.service.common;

import com.xuxd.kafka.exporter.config.KafkaConfig;
import com.xuxd.kafka.exporter.service.AbstractKafkaService;
import java.util.Properties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.stereotype.Component;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-11-08 19:31:09
 **/
@Component
public class KafkaClientHolder extends AbstractKafkaService {

    private final AdminClient adminClient;

    public KafkaClientHolder(KafkaConfig kafkaConfig) {
        super(kafkaConfig);

        Properties props = getProperties();
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConfig.getRequestTimeoutMs());

        adminClient = AdminClient.create(props);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> adminClient.close()));
    }

    public AdminClient getAdminClient() {
        return adminClient;
    }
}
