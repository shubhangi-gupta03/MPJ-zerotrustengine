package com.zerotrust.device_trust_service.service;

import com.zerotrust.common.dto.DeviceEvent;
import com.zerotrust.device_trust_service.dto.DeviceDtos;
import com.zerotrust.device_trust_service.entity.Device;
import com.zerotrust.device_trust_service.exception.ServiceException;
import com.zerotrust.device_trust_service.repository.DeviceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DeviceTrustService {
    private final DeviceRepository repository;
    private final DeviceTrustClassifier classifier;
    private final DeviceEventPublisher publisher;

    public DeviceTrustService(DeviceRepository repository, DeviceTrustClassifier classifier, DeviceEventPublisher publisher) {
        this.repository = repository;
        this.classifier = classifier;
        this.publisher = publisher;
    }

    public Device fingerprint(DeviceDtos.FingerprintRequest request) {
        DeviceDtos.TrustResult result = classifier.classify(request.features());
        Device device = repository.findByDeviceId(request.deviceId()).orElseGet(Device::new);
        device.setDeviceId(request.deviceId());
        device.setUserId(request.userId());
        device.setFingerprintHash("fp-" + Integer.toHexString(request.features().hashCode()));
        device.setTrustStatus(result.clazz());
        device.setTrustScore(result.trustScore());
        device.setLastSeenAt(Instant.now());
        if (device.getRegisteredAt() == null) {
            device.setRegisteredAt(Instant.now());
        }
        if (request.metadata() != null) {
            device.setMetadata(request.metadata());
        }
        Device saved = repository.save(device);
        publish(saved);
        return saved;
    }

    public List<Device> trustedDevices(String userId) {
        return repository.findByUserId(userId).stream().filter(d -> !d.isRevoked()).toList();
    }

    public Device register(String deviceId) {
        Device device = repository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Device not found"));
        device.setRevoked(false);
        device.setRevokedAt(null);
        device.setTrustStatus("TRUSTED");
        device.setTrustScore(Math.max(device.getTrustScore(), 0.8d));
        device.setLastSeenAt(Instant.now());
        Device saved = repository.save(device);
        publish(saved);
        return saved;
    }

    public Device revoke(String deviceId) {
        Device device = repository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Device not found"));
        device.setRevoked(true);
        device.setRevokedAt(Instant.now());
        device.setTrustStatus("UNTRUSTED");
        device.setTrustScore(0.1d);
        Device saved = repository.save(device);
        publish(saved);
        return saved;
    }

    public Device risk(String deviceId) {
        return repository.findByDeviceId(deviceId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND, "Device not found"));
    }

    private void publish(Device device) {
        DeviceEvent event = new DeviceEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setDeviceId(device.getDeviceId());
        event.setUserId(device.getUserId());
        event.setTrustStatus(device.getTrustStatus());
        event.setTrustScore(device.getTrustScore());
        event.setTimestamp(Instant.now());
        event.setMetadata(device.getMetadata());
        publisher.publish(event);
    }
}
