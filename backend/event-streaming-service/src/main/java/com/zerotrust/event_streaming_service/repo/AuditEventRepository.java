package com.zerotrust.event_streaming_service.repo;

import com.zerotrust.event_streaming_service.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {
    long countBySeverityIn(List<String> severities);
}
