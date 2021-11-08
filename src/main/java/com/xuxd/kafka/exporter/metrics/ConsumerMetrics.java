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
