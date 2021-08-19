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
