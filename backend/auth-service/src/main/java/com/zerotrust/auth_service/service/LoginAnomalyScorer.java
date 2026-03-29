package com.zerotrust.auth_service.service;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.Map;

@Service
public class LoginAnomalyScorer {
    private final OrtEnvironment environment;
    private final OrtSession session;

    public LoginAnomalyScorer(@Value("${auth.onnx.model-path}") String modelPath) throws OrtException {
        this.environment = OrtEnvironment.getEnvironment();
        this.session = environment.createSession(modelPath, new OrtSession.SessionOptions());
    }

    public double score(float[] features) {
        if (features == null || features.length != 6) {
            throw new IllegalArgumentException("Exactly 6 features are required");
        }
        try (OnnxTensor tensor = OnnxTensor.createTensor(environment, FloatBuffer.wrap(features), new long[]{1, 6});
             OrtSession.Result result = session.run(Map.of(session.getInputNames().iterator().next(), tensor))) {
            Object value = result.get(0).getValue();
            if (value instanceof float[][] arr && arr.length > 0 && arr[0].length > 0) {
                return arr[0][0];
            }
            return 0.0;
        } catch (OrtException e) {
            throw new IllegalStateException("Failed to score login anomaly", e);
        }
    }
}
