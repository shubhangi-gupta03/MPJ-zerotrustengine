package com.zerotrust.policy_engine_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
public class PolicyEngineServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PolicyEngineServiceApplication.class, args);
    }
}
