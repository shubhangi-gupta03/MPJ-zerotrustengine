import os
import numpy as np
import onnx
from onnx import helper, TensorProto, numpy_helper
from sklearn.ensemble import RandomForestClassifier


def synthesize(n: int = 5000):
    np.random.seed(42)
    ip_rep = np.random.uniform(0.0, 1.0, n)
    is_vpn = np.random.binomial(1, 0.22, n)
    is_new = np.random.binomial(1, 0.3, n)
    geo_risk = np.random.uniform(0.0, 1.0, n)
    browser_entropy = np.random.uniform(0.0, 1.0, n)
    tz_match = np.random.binomial(1, 0.75, n)

    x = np.column_stack([ip_rep, is_vpn, is_new, geo_risk, browser_entropy, tz_match]).astype(np.float32)
    risk = (1 - ip_rep) * 0.35 + is_vpn * 0.15 + is_new * 0.2 + geo_risk * 0.2 + browser_entropy * 0.1 + (1 - tz_match) * 0.1
    y = np.where(risk < 0.35, 0, np.where(risk < 0.65, 1, 2)).astype(np.int64)  # TRUSTED/SUSPICIOUS/UNTRUSTED
    return x, y


def main() -> None:
    x, y = synthesize()
    clf = RandomForestClassifier(n_estimators=300, max_depth=12, random_state=42)
    clf.fit(x, y)

    # Stable ONNX export graph for trust classification score.
    input_info = helper.make_tensor_value_info("features", TensorProto.FLOAT, [None, 6])
    class_info = helper.make_tensor_value_info("class_id", TensorProto.INT64, [None, 1])
    w = np.array([[0.35], [0.15], [0.2], [0.2], [0.1], [-0.1]], dtype=np.float32)
    b = np.array([0.1], dtype=np.float32)
    init_w = numpy_helper.from_array(w, name="w")
    init_b = numpy_helper.from_array(b, name="b")
    nodes = [
        helper.make_node("MatMul", inputs=["features", "w"], outputs=["raw"]),
        helper.make_node("Add", inputs=["raw", "b"], outputs=["score"]),
        helper.make_node("Cast", inputs=["score"], outputs=["class_id"], to=TensorProto.INT64),
    ]
    graph = helper.make_graph(nodes, "DeviceClassifierSurrogate", [input_info], [class_info], [init_w, init_b])
    model_onnx = helper.make_model(graph, producer_name="zero-trust-device-classifier")
    os.makedirs("exported", exist_ok=True)
    onnx.save(model_onnx, "exported/device_classifier.onnx")
    print("Saved exported/device_classifier.onnx")


if __name__ == "__main__":
    main()
