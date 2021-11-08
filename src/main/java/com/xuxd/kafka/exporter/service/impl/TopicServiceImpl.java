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
import com.xuxd.kafka.exporter.service.TopicService;
import com.xuxd.kafka.exporter.service.common.KafkaClientHolder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.springframework.stereotype.Service;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-11-08 19:23:18
 **/
@Slf4j
@Service
public class TopicServiceImpl extends AbstractKafkaService implements TopicService {

    private final AdminClient adminClient;

    public TopicServiceImpl(KafkaConfig kafkaConfig, KafkaClientHolder clientHolder) {
        super(kafkaConfig);
        this.adminClient = clientHolder.getAdminClient();
    }

    @Override public List<TopicPartition> getTopicPartitionList(Collection<String> topics) {
        Collection<String> searchTopics = new HashSet<>();
        if (CollectionUtils.isEmpty(topics)) {
            try {
                Set<String> strings = adminClient.listTopics(timeoutMs(new ListTopicsOptions())).names().get();
                searchTopics.addAll(strings);
            } catch (Exception e) {
                log.error("listTopics error.", e);
                return Collections.emptyList();
            }
        } else {
            searchTopics.addAll(topics);
        }

        try {
            Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(searchTopics, timeoutMs(new DescribeTopicsOptions()))
                .all().get();
            return topicDescriptionMap.values().stream()
                .flatMap(description -> description.partitions().stream().map(p -> new TopicPartition(description.name(), p.partition())))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("describeTopics error.", e);
            return Collections.emptyList();
        }

    }

    @Override public Map<TopicPartition, Long> getEndOffset(Collection<TopicPartition> topicPartitions) {

        if (CollectionUtils.isEmpty(topicPartitions)) {
            topicPartitions = getTopicPartitionList(null);
        }

        Properties props = getProperties();
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, INNER_CONSUMER);

        try (KafkaConsumer consumer = new KafkaConsumer(props, new ByteArrayDeserializer(), new ByteArrayDeserializer())) {
            return consumer.endOffsets(topicPartitions);
        }
    }
}
