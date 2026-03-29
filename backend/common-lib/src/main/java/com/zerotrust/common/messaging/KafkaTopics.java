package com.zerotrust.common.messaging;

public final class KafkaTopics {
    public static final String AUTH_EVENTS = "auth.events";
    public static final String DEVICE_EVENTS = "device.events";
    public static final String BEHAVIOR_ANOMALIES = "behavior.anomalies";
    public static final String RISK_UPDATES = "risk.updates";
    public static final String POLICY_DECISIONS = "policy.decisions";
    public static final String SYSTEM_ALERTS = "system.alerts";

    private KafkaTopics() {
    }
}
