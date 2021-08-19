package com.xuxd.kafka.exporter.collect;

import com.xuxd.kafka.exporter.metrics.ConsumerMetrics;
import com.xuxd.kafka.exporter.metrics.MetricsHelper;
import com.xuxd.kafka.exporter.metrics.MetricsReporter;
import com.xuxd.kafka.exporter.service.KafkaAdminService;
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

    private final KafkaAdminService adminService;

    private final MetricsReporter metricsReporter;

    public CollectTask(final KafkaAdminService adminService, final MetricsReporter metricsReporter) {
        this.adminService = adminService;
        this.metricsReporter = metricsReporter;
    }

    @Scheduled(cron = "${collect.cron.consumer}")
    public void collectConsumerInfo() {
        log.info("start collect consumer info");
        long startTime = System.currentTimeMillis();

        adminService.getGroupList().stream().forEach(groupId-> {

            Map<TopicPartition, Long> consumerLag = adminService.getConsumerLag(groupId);

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
