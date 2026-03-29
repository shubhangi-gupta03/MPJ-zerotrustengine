package com.zerotrust.device_trust_service.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.zerotrust.device_trust_service.dto.DeviceDtos;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public class DeviceTrustClassifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceTrustClassifier.class);
    private static final Map<Integer, String> CLASS_MAP = Map.of(0, "UNTRUSTED", 1, "LOW_TRUST", 2, "TRUSTED");
    private static final Map<String, Double> SCORE_MAP = Map.of("UNTRUSTED", 0.2, "LOW_TRUST", 0.55, "TRUSTED", 0.9);
    private final String modelPath;
    private OrtEnvironment environment;
    private OrtSession session;

    public DeviceTrustClassifier(@Value("${device.onnx.model-path}") String modelPath) {
        this.modelPath = modelPath;
    }

    @PostConstruct
    public void initialize() {
        try {
            environment = OrtEnvironment.getEnvironment();
            session = environment.createSession(Path.of(modelPath).toString(), new OrtSession.SessionOptions());
            LOGGER.info("Loaded device trust model from {}", modelPath);
        } catch (Exception ex) {
            LOGGER.warn("Device trust model unavailable at {}, fallback scoring enabled", modelPath);
            session = null;
        }
    }

    public DeviceDtos.TrustResult classify(List<Double> features) {
        if (features == null || features.size() != 6) {
            throw new IllegalArgumentException("Exactly 6 features required");
        }
        if (session == null) {
            return fallback(features);
        }
        try {
            float[][] input = new float[][]{toPrimitive(features)};
            String inputName = session.getInputNames().iterator().next();
            try (OnnxTensor tensor = OnnxTensor.createTensor(environment, input);
                 OrtSession.Result result = session.run(Map.of(inputName, tensor))) {
                Object value = result.get(0).getValue();
                int predicted = extractClass(value);
                String clazz = CLASS_MAP.getOrDefault(predicted, "LOW_TRUST");
                double confidence = Math.min(0.99d, Math.max(0.5d, mean(features)));
                return new DeviceDtos.TrustResult(clazz, confidence, SCORE_MAP.get(clazz));
            }
        } catch (OrtException ex) {
            LOGGER.error("ONNX inference failed, using fallback", ex);
            return fallback(features);
        }
    }

    private float[] toPrimitive(List<Double> features) {
        float[] arr = new float[features.size()];
        for (int i = 0; i < features.size(); i++) {
            arr[i] = features.get(i).floatValue();
        }
        return arr;
    }

    private int extractClass(Object value) {
        if (value instanceof long[] arr && arr.length > 0) {
            return (int) arr[0];
        }
        if (value instanceof float[][] arr2 && arr2.length > 0 && arr2[0].length > 0) {
            return arr2[0][0] > 0.66f ? 2 : (arr2[0][0] > 0.33f ? 1 : 0);
        }
        return 1;
    }

    private DeviceDtos.TrustResult fallback(List<Double> features) {
        double avg = mean(features);
        String clazz = avg >= 0.75 ? "TRUSTED" : (avg >= 0.45 ? "LOW_TRUST" : "UNTRUSTED");
        double confidence = Math.min(0.98d, Math.max(0.5d, avg));
        return new DeviceDtos.TrustResult(clazz, confidence, SCORE_MAP.get(clazz));
    }

    private double mean(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @PreDestroy
    public void close() {
        if (session != null) {
            try {
                session.close();
            } catch (OrtException ignored) {
            }
        }
    }
}
