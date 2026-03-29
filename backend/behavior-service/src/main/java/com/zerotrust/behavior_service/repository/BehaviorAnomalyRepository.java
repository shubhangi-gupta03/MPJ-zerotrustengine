package com.zerotrust.behavior_service.repository;

import com.zerotrust.behavior_service.entity.BehaviorAnomaly;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BehaviorAnomalyRepository extends MongoRepository<BehaviorAnomaly, String> {
    List<BehaviorAnomaly> findByUserId(String userId);
}
