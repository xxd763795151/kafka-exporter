package com.xuxd.kafka.exporter.config;

import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 10:38:41
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "metrics")
public class MetricsConfig {

    private Set<String> excludes;
}
