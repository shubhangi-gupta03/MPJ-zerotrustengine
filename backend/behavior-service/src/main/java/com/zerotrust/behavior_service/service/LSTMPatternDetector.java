package com.zerotrust.behavior_service.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
public class LSTMPatternDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(LSTMPatternDetector.class);
    private final String modelPath;
    private OrtEnvironment env;
    private OrtSession session;

    public LSTMPatternDetector(@Value("${behavior.layer3.model-path}") String modelPath) {
        this.modelPath = modelPath;
    }

    @PostConstruct
    public void init() {
        try {
            env = OrtEnvironment.getEnvironment();
            session = env.createSession(Path.of(modelPath).toString(), new OrtSession.SessionOptions());
        } catch (Exception ex) {
            LOGGER.warn("LSTM model not loaded, fallback mode");
            session = null;
        }
    }

    public double score(List<Integer> actionIds) {
        if (session == null) {
            return actionIds.stream().mapToInt(Integer::intValue).average().orElse(0.0) / 10.0;
        }
        try {
            long[][] input = new long[][]{actionIds.stream().mapToLong(Integer::longValue).toArray()};
            String inputName = session.getInputNames().iterator().next();
            try (OnnxTensor tensor = OnnxTensor.createTensor(env, input);
                 OrtSession.Result result = session.run(Map.of(inputName, tensor))) {
                Object value = result.get(0).getValue();
                if (value instanceof float[][] out && out.length > 0 && out[0].length > 0) {
                    return Math.max(0.0d, Math.min(1.0d, out[0][0]));
                }
            }
        } catch (Exception ignored) {
        }
        return 0.0d;
    }
}
