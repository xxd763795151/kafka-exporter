package com.xuxd.kafka.exporter.metrics;

import lombok.Getter;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 10:55:08
 **/
public enum ConsumerMetrics {

    CONSUMER_LAG("consumer-lag", "consumer lag", "topic", "", "partition", "", "groupId", "");

    @Getter
    private String name;

    private String[] labels;

    @Getter
    private String description;

    ConsumerMetrics(String name, String description, String... labels) {
        this.name = name;
        this.description = description;
        this.labels = labels;
        MetricsHelper.METRICS_DESCRIPTION_CACHE.put(name, description);
    }

    public String[] getLabels() {
        return MetricsHelper.copyLabels(this.labels);
    }
}
