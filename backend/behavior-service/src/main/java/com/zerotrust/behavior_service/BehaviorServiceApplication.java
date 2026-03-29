package com.zerotrust.behavior_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableScheduling
public class BehaviorServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BehaviorServiceApplication.class, args);
    }
}
