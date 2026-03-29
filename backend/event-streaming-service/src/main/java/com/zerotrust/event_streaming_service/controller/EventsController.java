package com.zerotrust.event_streaming_service.controller;

import com.zerotrust.common.api.ApiResponse;
import com.zerotrust.event_streaming_service.repo.AuditEventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventsController {
    private final AuditEventRepository repository;

    public EventsController(AuditEventRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        long alerts = repository.countBySeverityIn(List.of("HIGH", "CRITICAL"));
        return ApiResponse.ok("Event streaming service healthy", Map.of(
                "storedEvents", repository.count(),
                "highOrCriticalAlerts", alerts
        ));
    }
}
