package com.zerotrust.policy_engine_service.dto;

import com.zerotrust.policy_engine_service.entity.PolicyRule;

import java.util.Map;

public class PolicyDtos {
    public record EvaluateRequest(String userId, String role, String riskTier, String resource, Integer hour, Map<String, Object> context) { }
    public record EvaluateResult(String action, String ruleId) { }
    public record SimulateRequest(EvaluateRequest evaluateRequest) { }
    public record RuleUpsertRequest(PolicyRule rule) { }
}
