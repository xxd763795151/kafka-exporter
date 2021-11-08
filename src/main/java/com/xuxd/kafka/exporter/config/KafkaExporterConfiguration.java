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
