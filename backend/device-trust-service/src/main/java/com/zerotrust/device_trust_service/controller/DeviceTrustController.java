package com.zerotrust.device_trust_service.controller;

import com.zerotrust.common.api.ApiResponse;
import com.zerotrust.device_trust_service.dto.DeviceDtos;
import com.zerotrust.device_trust_service.entity.Device;
import com.zerotrust.device_trust_service.service.DeviceTrustService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-trust")
public class DeviceTrustController {
    private final DeviceTrustService service;

    public DeviceTrustController(DeviceTrustService service) {
        this.service = service;
    }

    @PostMapping("/fingerprint")
    public ResponseEntity<ApiResponse<Device>> fingerprint(@Valid @RequestBody DeviceDtos.FingerprintRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Fingerprint processed", service.fingerprint(request)));
    }

    @GetMapping("/trusted/{userId}")
    public ResponseEntity<ApiResponse<List<Device>>> trusted(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok("Trusted devices fetched", service.trustedDevices(userId)));
    }

    @PostMapping("/register/{deviceId}")
    public ResponseEntity<ApiResponse<Device>> register(@PathVariable String deviceId) {
        return ResponseEntity.ok(ApiResponse.ok("Device registered", service.register(deviceId)));
    }

    @PostMapping("/revoke/{deviceId}")
    public ResponseEntity<ApiResponse<Device>> revoke(@PathVariable String deviceId) {
        return ResponseEntity.ok(ApiResponse.ok("Device revoked", service.revoke(deviceId)));
    }

    @GetMapping("/risk/{deviceId}")
    public ResponseEntity<ApiResponse<Device>> risk(@PathVariable String deviceId) {
        return ResponseEntity.ok(ApiResponse.ok("Device risk fetched", service.risk(deviceId)));
    }
}
