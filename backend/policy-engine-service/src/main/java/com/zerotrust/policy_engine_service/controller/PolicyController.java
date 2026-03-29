package com.zerotrust.policy_engine_service.controller;

import com.zerotrust.common.api.ApiResponse;
import com.zerotrust.policy_engine_service.dto.PolicyDtos;
import com.zerotrust.policy_engine_service.entity.PolicyDecision;
import com.zerotrust.policy_engine_service.entity.PolicyRule;
import com.zerotrust.policy_engine_service.service.PolicyEngineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {
    private final PolicyEngineService service;

    public PolicyController(PolicyEngineService service) {
        this.service = service;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<PolicyDtos.EvaluateResult>> evaluate(@RequestBody PolicyDtos.EvaluateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Policy evaluated", service.evaluate(request, true)));
    }

    @GetMapping("/rules")
    public ResponseEntity<ApiResponse<List<PolicyRule>>> listRules() {
        return ResponseEntity.ok(ApiResponse.ok("Rules fetched", service.listRules()));
    }

    @PostMapping("/rules")
    public ResponseEntity<ApiResponse<PolicyRule>> createRule(@RequestBody PolicyRule rule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Rule created", service.createRule(rule)));
    }

    @PutMapping("/rules/{id}")
    public ResponseEntity<ApiResponse<PolicyRule>> updateRule(@PathVariable String id, @RequestBody PolicyRule rule) {
        return ResponseEntity.ok(ApiResponse.ok("Rule updated", service.updateRule(id, rule)));
    }

    @GetMapping("/decision-log")
    public ResponseEntity<ApiResponse<List<PolicyDecision>>> log() {
        return ResponseEntity.ok(ApiResponse.ok("Decision log fetched", service.decisionLog()));
    }

    @PostMapping("/simulate")
    public ResponseEntity<ApiResponse<PolicyDtos.EvaluateResult>> simulate(@RequestBody PolicyDtos.SimulateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Simulation complete", service.evaluate(request.evaluateRequest(), false)));
    }
}
