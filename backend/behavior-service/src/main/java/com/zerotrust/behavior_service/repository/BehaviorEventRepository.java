package com.zerotrust.behavior_service.repository;

import com.zerotrust.behavior_service.entity.BehaviorEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface BehaviorEventRepository extends MongoRepository<BehaviorEventEntity, String> {
    List<BehaviorEventEntity> findByUserId(String userId);
    List<BehaviorEventEntity> findByUserIdAndEventTimeAfter(String userId, Instant time);
}
