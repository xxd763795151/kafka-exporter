package com.xuxd.kafka.exporter.service;

import java.util.List;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:33:08
 **/
public interface KafkaAdminService {

    List<String> getGroupList();

    Map<TopicPartition, Long> getConsumerLag(String groupId);
}
