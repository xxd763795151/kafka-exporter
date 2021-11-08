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

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AtomicDouble;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 14:13:56
 **/
@Component
public class MetricsReporter {

    private final MeterRegistry registry;

    private final ConcurrentHashMap<String, AtomicDouble> gaugeCache = new ConcurrentHashMap<>();

    public MetricsReporter(final MeterRegistry registry) {
        this.registry = registry;
    }

    public void reportGauge(String name, String[] labels, double value) {
        Preconditions.checkNotNull(labels, "labels is null");
        Preconditions.checkArgument((labels.length & 1) == 0);

        String uniqueID = MetricsHelper.uniqueID(name, labels);
        if (gaugeCache.containsKey(uniqueID)) {
            gaugeCache.get(uniqueID).set(value);
        } else {
            AtomicDouble supplier = new AtomicDouble(value);
            Gauge.builder(name, () -> supplier)
                .tags(Tags.of(labels)).description(MetricsHelper.METRICS_DESCRIPTION_CACHE.get(name)).register(registry);
            gaugeCache.putIfAbsent(uniqueID, supplier);
        }
//        registry.gauge(name, Tags.of(labels), value);
    }

}
