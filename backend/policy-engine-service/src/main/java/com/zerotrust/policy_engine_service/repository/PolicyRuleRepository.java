package com.zerotrust.policy_engine_service.repository;

import com.zerotrust.policy_engine_service.entity.PolicyRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PolicyRuleRepository extends MongoRepository<PolicyRule, String> {
    List<PolicyRule> findByEnabledTrueOrderByPriorityAsc();
}
