package com.zerotrust.behavior_service.controller;

import com.zerotrust.behavior_service.dto.BehaviorDtos;
import com.zerotrust.behavior_service.entity.BehaviorAnomaly;
import com.zerotrust.behavior_service.entity.BehaviorBaseline;
import com.zerotrust.behavior_service.entity.BehaviorEventEntity;
import com.zerotrust.behavior_service.service.BehaviorService;
import com.zerotrust.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/behavior")
public class BehaviorController {
    private final BehaviorService behaviorService;

    public BehaviorController(BehaviorService behaviorService) {
        this.behaviorService = behaviorService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<ApiResponse<BehaviorEventEntity>> ingest(@Valid @RequestBody BehaviorDtos.IngestRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Behavior event ingested", behaviorService.ingest(request)));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<List<BehaviorEventEntity>>> profile(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Behavior profile fetched", behaviorService.profile(userId)));
    }

    @GetMapping("/anomalies/{userId}")
    public ResponseEntity<ApiResponse<List<BehaviorAnomaly>>> anomalies(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Behavior anomalies fetched", behaviorService.anomalies(userId)));
    }

    @GetMapping("/baseline")
    public ResponseEntity<ApiResponse<List<BehaviorBaseline>>> baseline() {
        return ResponseEntity.ok(ApiResponse.ok("Baselines fetched", behaviorService.baselines()));
    }

    @PostMapping("/baseline/rebuild")
    public ResponseEntity<ApiResponse<List<BehaviorBaseline>>> rebuild() {
        return ResponseEntity.ok(ApiResponse.ok("Baselines rebuilt", behaviorService.rebuildBaselines()));
    }
}
