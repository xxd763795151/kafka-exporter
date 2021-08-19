package com.xuxd.kafka.exporter.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.config.MeterFilterReply;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 10:35:52
 **/
@Configuration
public class KafkaExporterConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(final ObjectProvider<MetricsConfig> metricsConfig) {
        return registry -> registry.config().meterFilter(new MeterFilter() {
            @Override public MeterFilterReply accept(Meter.Id id) {
                return metricsConfig.getIfAvailable(() -> new MetricsConfig()).getExcludes().contains(id.getName()) ? MeterFilterReply.DENY
                    : MeterFilterReply.ACCEPT;
            }
        });
    }
}
