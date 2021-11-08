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
import com.xuxd.kafka.exporter.service.ConsumerService;
import com.xuxd.kafka.exporter.service.TopicService;
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

    private final TopicService topicService;

    private final ConsumerService consumerService;

    private final MetricsReporter metricsReporter;

    public CollectTask(final TopicService topicService, final ConsumerService consumerService,
        final MetricsReporter metricsReporter) {
        this.topicService = topicService;
        this.consumerService = consumerService;
        this.metricsReporter = metricsReporter;
    }

    @Scheduled(cron = "${collect.cron.consumer}")
    public void collectConsumerInfo() {
        log.info("start collect consumer info");
        long startTime = System.currentTimeMillis();

        Map<TopicPartition, Long> endOffsetMap = topicService.getEndOffset(null);

        for (String groupId : consumerService.getGroupList()) {

            Map<TopicPartition, Long> committedOffsetMap = consumerService.getCommittedOffset(groupId);
            committedOffsetMap.forEach((topicPartition, committedOffset) -> {
                long endOffset = endOffsetMap.get(topicPartition);
                long lag = endOffset - committedOffset;
                // 上报消费积压信息
                String[] labels = ConsumerMetrics.CONSUMER_LAG.getLabels();
                MetricsHelper.updateLabelValue(labels, "topic", topicPartition.topic());
                MetricsHelper.updateLabelValue(labels, "partition", String.valueOf(topicPartition.partition()));
                MetricsHelper.updateLabelValue(labels, "groupId", groupId);
                metricsReporter.reportGauge(ConsumerMetrics.CONSUMER_LAG.getName(), labels, Double.valueOf(lag));

                // 上报消费位点，采集消费位点是为了变相的计算消费端的tps，因为目前还不能直接获取消费端的消费tps相关指标
                // 所以通过间接的方式，根据消费位点的增长速率来计算消费端的消费tps，如果出现消费tps猛增的话（可能是积压太多，突然消费，如果出现读取历史数据），可以考虑及时预警，作相关处理
                // 消费位点不能使用Counter类型，因为可以重置，所以不能保证一定是时刻增长
                labels = ConsumerMetrics.CONSUMER_OFFSET.getLabels();
                MetricsHelper.updateLabelValue(labels, "topic", topicPartition.topic());
                MetricsHelper.updateLabelValue(labels, "partition", String.valueOf(topicPartition.partition()));
                MetricsHelper.updateLabelValue(labels, "groupId", groupId);
                metricsReporter.reportGauge(ConsumerMetrics.CONSUMER_OFFSET.getName(), labels, Double.valueOf(committedOffset));
            });
        }

        log.info("end collect consumer info, cost time: {}", System.currentTimeMillis() - startTime);
    }
}
