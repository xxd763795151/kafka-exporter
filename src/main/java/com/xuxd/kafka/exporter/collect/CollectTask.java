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
package com.xuxd.kafka.exporter.collect;

import com.xuxd.kafka.exporter.metrics.ConsumerMetrics;
import com.xuxd.kafka.exporter.metrics.MetricsHelper;
import com.xuxd.kafka.exporter.metrics.MetricsReporter;
import com.xuxd.kafka.exporter.service.KafkaConsumerService;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:11:46
 **/
@Slf4j
@Component
public class CollectTask {

    private final KafkaConsumerService consumerService;

    private final MetricsReporter metricsReporter;

    public CollectTask(final KafkaConsumerService consumerService, final MetricsReporter metricsReporter) {
        this.consumerService = consumerService;
        this.metricsReporter = metricsReporter;
    }

    @Scheduled(cron = "${collect.cron.consumer}")
    public void collectConsumerInfo() {
        log.info("start collect consumer info");
        long startTime = System.currentTimeMillis();

        for (String groupId : consumerService.getGroupList()) {
            Map<TopicPartition, Long> consumerLag = consumerService.getConsumerLag(groupId);

            consumerLag.forEach((partition, lag) -> {
                String[] labels = ConsumerMetrics.CONSUMER_LAG.getLabels();
                MetricsHelper.updateLabelValue(labels, "topic", partition.topic());
                MetricsHelper.updateLabelValue(labels, "partition", String.valueOf(partition.partition()));
                MetricsHelper.updateLabelValue(labels, "groupId", groupId);
                metricsReporter.reportGauge(ConsumerMetrics.CONSUMER_LAG.getName(), labels, Double.valueOf(lag));
            });
        }

        log.info("end collect consumer info, cost time: {}", System.currentTimeMillis() - startTime);
    }
}
