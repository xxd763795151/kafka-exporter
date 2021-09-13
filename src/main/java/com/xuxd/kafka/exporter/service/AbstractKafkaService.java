package com.xuxd.kafka.exporter.service;

import com.xuxd.kafka.exporter.config.KafkaConfig;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.config.SaslConfigs;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-09-13 14:06:05
 **/
public abstract class AbstractKafkaService {

    private KafkaConfig kafkaConfig;

    public AbstractKafkaService(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    protected Properties getProperties() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootServer());
        if (kafkaConfig.isEnableAcl()) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaConfig.getSecurityProtocol());
            props.put(SaslConfigs.SASL_MECHANISM, kafkaConfig.getSaslMechanism());
            props.put(SaslConfigs.SASL_JAAS_CONFIG, kafkaConfig.getSaslJaasConfig());
        }

        return props;
    }
}
