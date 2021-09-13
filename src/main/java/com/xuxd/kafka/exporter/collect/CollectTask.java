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

        consumerService.getGroupList().stream().forEach(groupId-> {

            Map<TopicPartition, Long> consumerLag = consumerService.getConsumerLag(groupId);

            consumerLag.forEach((partition, lag) -> {
                String[] labels = ConsumerMetrics.CONSUMER_LAG.getLabels();
                MetricsHelper.updateLabelValue(labels, "topic", partition.topic());
                MetricsHelper.updateLabelValue(labels, "partition", String.valueOf(partition.partition()));
                MetricsHelper.updateLabelValue(labels, "groupId", groupId);
                metricsReporter.reportGauge(ConsumerMetrics.CONSUMER_LAG.getName(), labels, Double.valueOf(lag));
            });
        });

        log.info("end collect consumer info, cost time: {}", System.currentTimeMillis() - startTime);
    }
}
