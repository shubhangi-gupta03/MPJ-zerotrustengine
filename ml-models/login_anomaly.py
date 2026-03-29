import os
import numpy as np
import onnx
from onnx import helper, TensorProto, numpy_helper
from sklearn.linear_model import LogisticRegression


def main() -> None:
    np.random.seed(42)
    n = 6000
    hour = np.random.randint(0, 24, n)
    day_of_week = np.random.randint(0, 7, n)
    ip_risk = np.random.uniform(0.0, 1.0, n)
    is_new_ip = np.random.binomial(1, 0.25, n)
    failed_attempts = np.random.poisson(1.2, n)
    distance_km = np.abs(np.random.normal(80, 220, n))

    x = np.column_stack([hour, day_of_week, ip_risk, is_new_ip, failed_attempts, distance_km]).astype(np.float32)
    score = (
        (np.where((hour < 6) | (hour > 22), 1, 0) * 0.2) +
        ip_risk * 0.25 +
        is_new_ip * 0.2 +
        np.clip(failed_attempts / 5.0, 0, 1) * 0.2 +
        np.clip(distance_km / 1500.0, 0, 1) * 0.15
    )
    y = (score > 0.5).astype(np.int64)

    model = LogisticRegression(max_iter=500, random_state=42)
    model.fit(x, y)

    coef = model.coef_.astype(np.float32).T
    intercept = model.intercept_.astype(np.float32)
    init_w = numpy_helper.from_array(coef, name="w")
    init_b = numpy_helper.from_array(intercept, name="b")

    inp = helper.make_tensor_value_info("features", TensorProto.FLOAT, [None, 6])
    out = helper.make_tensor_value_info("anomaly_probability", TensorProto.FLOAT, [None, 1])
    nodes = [
        helper.make_node("MatMul", inputs=["features", "w"], outputs=["z"]),
        helper.make_node("Add", inputs=["z", "b"], outputs=["z2"]),
        helper.make_node("Sigmoid", inputs=["z2"], outputs=["anomaly_probability"]),
    ]
    graph = helper.make_graph(nodes, "LoginAnomalyLogReg", [inp], [out], [init_w, init_b])
    onx = helper.make_model(graph, producer_name="zero-trust-login-anomaly")
    os.makedirs("exported", exist_ok=True)
    onnx.save(onx, "exported/login_anomaly.onnx")
    print("Saved exported/login_anomaly.onnx")


if __name__ == "__main__":
    main()
