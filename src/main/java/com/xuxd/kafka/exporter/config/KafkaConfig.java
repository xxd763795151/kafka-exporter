package com.xuxd.kafka.exporter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 11:30:49
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {

    private String bootServer;

    private int requestTimeoutMs;

    private String securityProtocol;

    private String saslMechanism;

    private String saslJaasConfig;

    private boolean enableAcl;
}
