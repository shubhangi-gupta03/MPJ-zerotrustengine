package com.zerotrust.auth_service.service;

import com.zerotrust.auth_service.entity.User;
import com.zerotrust.auth_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void seed() {
        if (userRepository.count() == 0) {
            for (int i = 1; i <= 10; i++) {
                User user = new User();
                user.setUsername("demo" + i);
                user.setEmail("demo" + i + "@example.com");
                user.setPasswordHash(passwordEncoder.encode("Password@123"));
                user.setMfaEnabled(i % 2 == 0);
                userRepository.save(user);
            }
        }

        if (mongoTemplate.getCollection("risk_scores").countDocuments() == 0) {
            Instant now = Instant.now();
            for (int i = 0; i < 200; i++) {
                Map<String, Object> device = new HashMap<>();
                device.put("deviceId", "dev-" + i);
                device.put("user", "demo" + ((i % 10) + 1));
                device.put("trusted", i % 3 != 0);
                device.put("createdAt", now.minus(i % 7, ChronoUnit.DAYS));
                mongoTemplate.insert(device, "devices");

                Map<String, Object> baseline = new HashMap<>();
                baseline.put("user", "demo" + ((i % 10) + 1));
                baseline.put("hourOfDay", i % 24);
                baseline.put("ipCluster", i % 8);
                baseline.put("updatedAt", now.minus(i % 7, ChronoUnit.DAYS));
                mongoTemplate.insert(baseline, "behavior_baselines");

                Map<String, Object> risk = new HashMap<>();
                risk.put("user", "demo" + ((i % 10) + 1));
                risk.put("score", ThreadLocalRandom.current().nextDouble(0.1, 0.95));
                risk.put("timestamp", now.minus(i % 7, ChronoUnit.DAYS).minus(i, ChronoUnit.HOURS));
                mongoTemplate.insert(risk, "risk_scores");
            }
        }
    }
}
