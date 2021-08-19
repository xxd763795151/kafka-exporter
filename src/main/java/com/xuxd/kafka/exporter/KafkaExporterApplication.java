package com.xuxd.kafka.exporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * kafka-exporter.
 *
 * @author xuxd
 * @date 2021-08-19 10:31:20
 **/
@SpringBootApplication
@EnableScheduling
public class KafkaExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaExporterApplication.class, args);
    }

}
