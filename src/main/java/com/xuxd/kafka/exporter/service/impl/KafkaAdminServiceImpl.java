package com.xuxd.kafka.exporter.service.impl;

import com.xuxd.kafka.exporter.config.KafkaConfig;
import com.xuxd.kafka.exporter.service.KafkaAdminService;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
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
public class KafkaAdminServiceImpl implements KafkaAdminService {

    private final KafkaConfig kafkaConfig;

    private final AdminClient adminClient;

    public KafkaAdminServiceImpl(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        Properties props = new Properties();

        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootServer());
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConfig.getRequestTimeoutMs());
        adminClient = AdminClient.create(props);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> adminClient.close()));
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

            Properties props = new Properties();
            props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootServer());
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
}
