package com.zerotrust.policy_engine_service.service;

import com.zerotrust.policy_engine_service.dto.PolicyDtos;
import com.zerotrust.policy_engine_service.entity.PolicyDecision;
import com.zerotrust.policy_engine_service.entity.PolicyRule;
import com.zerotrust.policy_engine_service.repository.PolicyDecisionRepository;
import com.zerotrust.policy_engine_service.repository.PolicyRuleRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

@Service
public class PolicyEngineService {
    private final PolicyRuleRepository ruleRepository;
    private final PolicyDecisionRepository decisionRepository;
    private final PolicyDecisionPublisher publisher;

    public PolicyEngineService(PolicyRuleRepository ruleRepository, PolicyDecisionRepository decisionRepository, PolicyDecisionPublisher publisher) {
        this.ruleRepository = ruleRepository;
        this.decisionRepository = decisionRepository;
        this.publisher = publisher;
    }

    public PolicyDtos.EvaluateResult evaluate(PolicyDtos.EvaluateRequest request, boolean persist) {
        List<PolicyRule> rules = ruleRepository.findByEnabledTrueOrderByPriorityAsc();
        PolicyRule matched = rules.stream().filter(rule -> matches(rule, request)).findFirst().orElse(null);
        String action = matched == null ? "DENY" : matched.getAction();
        if (persist) {
            PolicyDecision decision = new PolicyDecision();
            decision.setUserId(request.userId());
            decision.setRole(request.role());
            decision.setResource(request.resource());
            decision.setRiskTier(request.riskTier());
            decision.setAction(action);
            decision.setMatchedRuleId(matched == null ? null : matched.getId());
            decision.setContext(request.context());
            decision.setDecidedAt(Instant.now());
            publisher.publish(decisionRepository.save(decision));
        }
        return new PolicyDtos.EvaluateResult(action, matched == null ? null : matched.getId());
    }

    public List<PolicyRule> listRules() { return ruleRepository.findAll(); }
    public PolicyRule createRule(PolicyRule rule) { rule.setUpdatedAt(Instant.now()); return ruleRepository.save(rule); }
    public PolicyRule updateRule(String id, PolicyRule updated) { updated.setId(id); updated.setUpdatedAt(Instant.now()); return ruleRepository.save(updated); }
    public List<PolicyDecision> decisionLog() { return decisionRepository.findAll(); }

    private boolean matches(PolicyRule rule, PolicyDtos.EvaluateRequest request) {
        int hour = request.hour() == null ? LocalTime.now().getHour() : request.hour();
        return matchString(rule.getUserId(), request.userId())
                && matchString(rule.getRole(), request.role())
                && matchString(rule.getRiskTier(), request.riskTier())
                && matchString(rule.getResource(), request.resource())
                && matchTime(rule.getFromHour(), rule.getToHour(), hour);
    }

    private boolean matchString(String ruleValue, String inputValue) {
        return ruleValue == null || ruleValue.isBlank() || "*".equals(ruleValue) || ruleValue.equalsIgnoreCase(inputValue);
    }

    private boolean matchTime(Integer from, Integer to, int hour) {
        if (from == null || to == null) {
            return true;
        }
        return hour >= from && hour <= to;
    }
}
