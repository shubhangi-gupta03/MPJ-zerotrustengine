package com.zerotrust.policy_engine_service.repository;

import com.zerotrust.policy_engine_service.entity.PolicyDecision;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PolicyDecisionRepository extends MongoRepository<PolicyDecision, String> {
}
