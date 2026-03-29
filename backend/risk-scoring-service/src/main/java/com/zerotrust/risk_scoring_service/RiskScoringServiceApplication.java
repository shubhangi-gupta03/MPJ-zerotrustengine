package com.zerotrust.risk_scoring_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableRetry
public class RiskScoringServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiskScoringServiceApplication.class, args);
    }
}
