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
package com.xuxd.kafka.exporter.service.impl;

import com.xuxd.kafka.exporter.config.KafkaConfig;
import com.xuxd.kafka.exporter.service.AbstractKafkaService;
import com.xuxd.kafka.exporter.service.ConsumerService;
import com.xuxd.kafka.exporter.service.common.KafkaClientHolder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsOptions;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Service;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:34:09
 **/
@Service
@Slf4j
public class ConsumerServiceImpl extends AbstractKafkaService implements ConsumerService {

    private final AdminClient adminClient;

    public ConsumerServiceImpl(KafkaConfig kafkaConfig, KafkaClientHolder clientHolder) {
        super(kafkaConfig);
        this.adminClient = clientHolder.getAdminClient();
    }

    @Override public List<String> getGroupList() {
        ListConsumerGroupsResult result = adminClient.listConsumerGroups();
        try {
            return result.all().get().stream().filter(c -> c.state().isPresent() && c.state().get() != ConsumerGroupState.DEAD)
                .map(ConsumerGroupListing::groupId).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("getGroupList error", e);
            return Collections.EMPTY_LIST;
        }
    }

    @Override public Map<TopicPartition, Long> getConsumerLag(String groupId) {
        ListConsumerGroupOffsetsResult consumerGroupOffsets = adminClient.listConsumerGroupOffsets(groupId);
        try {
            Map<TopicPartition, OffsetAndMetadata> consumeOffsetMap = consumerGroupOffsets.partitionsToOffsetAndMetadata().get(3, TimeUnit.SECONDS);

            Properties props = getProperties();
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);) {
                Map<TopicPartition, Long> endOffsetMap = consumer.endOffsets(consumeOffsetMap.keySet());

                Map<TopicPartition, Long> result = new HashMap<>();
                consumeOffsetMap.forEach((k, v) -> {
                    if (endOffsetMap.containsKey(k)) {
                        result.put(k, endOffsetMap.get(k) - v.offset());
                    }
                });

                return result;
            }

        } catch (Exception e) {
            log.error("getConsumerLag error", e);
        }

        return Collections.emptyMap();
    }

    @Override public Map<TopicPartition, Long> getCommittedOffset(String groupId) {
        Map<TopicPartition, Long> res = new HashMap<>();

        try {
            Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap = adminClient
                .listConsumerGroupOffsets(groupId, timeoutMs(new ListConsumerGroupOffsetsOptions())).partitionsToOffsetAndMetadata().get();
            offsetAndMetadataMap.forEach((t, o) -> res.put(t, o.offset()));
        } catch (Exception e) {
            log.error("listConsumerGroupOffsets error", e);
            return Collections.emptyMap();
        }

        return res;
    }
}
