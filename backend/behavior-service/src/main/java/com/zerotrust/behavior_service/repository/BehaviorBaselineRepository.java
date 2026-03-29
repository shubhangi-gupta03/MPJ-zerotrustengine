package com.zerotrust.behavior_service.repository;

import com.zerotrust.behavior_service.entity.BehaviorBaseline;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BehaviorBaselineRepository extends MongoRepository<BehaviorBaseline, String> {
    Optional<BehaviorBaseline> findByUserId(String userId);
}
