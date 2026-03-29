package com.zerotrust.risk_scoring_service.controller;

import com.zerotrust.common.api.ApiResponse;
import com.zerotrust.risk_scoring_service.dto.RiskDtos;
import com.zerotrust.risk_scoring_service.entity.RiskScore;
import com.zerotrust.risk_scoring_service.service.RiskScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
public class RiskController {
    private final RiskScoringService service;

    public RiskController(RiskScoringService service) {
        this.service = service;
    }

    @GetMapping("/score/{sessionId}")
    public ResponseEntity<ApiResponse<RiskScore>> score(@PathVariable String sessionId) {
        return ResponseEntity.ok(ApiResponse.ok("Risk score fetched", service.score(sessionId)));
    }

    @PostMapping("/compute")
    public ResponseEntity<ApiResponse<RiskScore>> compute(@RequestBody RiskDtos.ComputeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Risk computed", service.compute(request)));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<ApiResponse<List<RiskScore>>> history(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Risk history fetched", service.history(userId)));
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> summary() {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard summary fetched", service.dashboardSummary()));
    }

    @PutMapping("/weights/update")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@RequestBody RiskDtos.WeightsUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Weights updated", service.updateWeights(request)));
    }
}
