package com.zerotrust.risk_scoring_service.repository;

import com.zerotrust.risk_scoring_service.entity.RiskScore;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RiskScoreRepository extends MongoRepository<RiskScore, String> {
    Optional<RiskScore> findTopBySessionIdOrderByCalculatedAtDesc(String sessionId);
    List<RiskScore> findByUserIdOrderByCalculatedAtDesc(String userId);
    List<RiskScore> findBySessionIdAndActiveTrue(String sessionId);
}
