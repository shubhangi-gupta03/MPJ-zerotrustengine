package com.zerotrust.device_trust_service.repository;

import com.zerotrust.device_trust_service.entity.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {
    Optional<Device> findByDeviceId(String deviceId);
    List<Device> findByUserId(String userId);
}
