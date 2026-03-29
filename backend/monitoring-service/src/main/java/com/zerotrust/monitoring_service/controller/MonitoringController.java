package com.zerotrust.monitoring_service.controller;

import com.zerotrust.common.api.ApiResponse;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitoringController {
    private final MongoTemplate mongoTemplate;

    public MonitoringController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> stats() {
        long alerts = mongoTemplate.count(
                Query.query(Criteria.where("severity").in(List.of("HIGH", "CRITICAL"))),
                "audit_events"
        );
        double avgRisk = mongoTemplate.findAll(Map.class, "risk_scores").stream()
                .mapToDouble(m -> ((Number) m.getOrDefault("compositeScore", 0)).doubleValue())
                .average().orElse(0);
        Map<String, Object> data = new HashMap<>();
        data.put("activeSessions", mongoTemplate.count(new Query(), "risk_scores"));
        data.put("alerts", alerts);
        data.put("blockedCount24h", mongoTemplate.count(Query.query(Criteria.where("action").is("DENY")), "audit_events"));
        data.put("avgRisk", Math.round(avgRisk * 100.0) / 100.0);
        return ApiResponse.ok("Monitoring stats", data);
    }

    @GetMapping("/audit/{userId}")
    public ApiResponse<List<Map>> audit(@PathVariable String userId) {
        Query q = Query.query(Criteria.where("userId").is(userId)).with(Sort.by(Sort.Direction.DESC, "timestamp"));
        return ApiResponse.ok("User audit timeline", mongoTemplate.find(q, Map.class, "audit_events"));
    }

    @GetMapping("/threat-feed")
    public ApiResponse<List<Map>> threatFeed() {
        Query q = Query.query(Criteria.where("severity").in(List.of("HIGH", "CRITICAL")))
                .with(Sort.by(Sort.Direction.DESC, "timestamp"))
                .limit(50);
        return ApiResponse.ok("Threat feed", mongoTemplate.find(q, Map.class, "audit_events"));
    }

    @GetMapping("/reports/weekly")
    public ApiResponse<Map<String, Object>> weeklyReport() {
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        Query q = Query.query(Criteria.where("timestamp").gte(weekAgo));
        List<Map> weekly = mongoTemplate.find(q, Map.class, "audit_events");
        Map<String, Object> report = new HashMap<>();
        report.put("generatedAt", Instant.now().toString());
        report.put("totalEvents", weekly.size());
        report.put("criticalEvents", weekly.stream().filter(e -> "CRITICAL".equals(e.get("severity"))).count());
        report.put("highEvents", weekly.stream().filter(e -> "HIGH".equals(e.get("severity"))).count());
        return ApiResponse.ok("Weekly security summary", report);
    }
}
