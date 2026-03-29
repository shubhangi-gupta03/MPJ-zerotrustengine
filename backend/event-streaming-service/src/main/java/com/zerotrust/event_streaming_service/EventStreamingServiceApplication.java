package com.zerotrust.event_streaming_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
public class EventStreamingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventStreamingServiceApplication.class, args);
    }
}
