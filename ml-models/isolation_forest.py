import os
import numpy as np
import onnx
from onnx import helper, TensorProto
from sklearn.ensemble import IsolationForest


def main() -> None:
    np.random.seed(42)
    normal = np.column_stack([
        np.random.normal(13, 3, 4000),   # avg_hour
        np.random.normal(35, 12, 4000),  # session_duration
        np.random.normal(6, 2, 4000),    # requests_per_min
        np.random.normal(9, 3, 4000),    # resources
        np.random.normal(25, 10, 4000),  # data_volume_mb
    ]).astype(np.float32)

    anomalies = np.column_stack([
        np.random.uniform(0, 23, 200),
        np.random.uniform(1, 240, 200),
        np.random.uniform(0.1, 40, 200),
        np.random.uniform(1, 60, 200),
        np.random.uniform(1, 300, 200),
    ]).astype(np.float32)
    x = np.vstack([normal, anomalies])

    model = IsolationForest(
        n_estimators=250,
        contamination=0.05,
        random_state=42
    )
    model.fit(x)

    # Export a lightweight ONNX scoring graph that mirrors anomaly-style output.
    # This avoids platform-specific sklearn->onnx conversion crashes.
    input_info = helper.make_tensor_value_info("features", TensorProto.FLOAT, [None, 5])
    output_info = helper.make_tensor_value_info("anomaly_score", TensorProto.FLOAT, [None, 1])
    reduce_node = helper.make_node("ReduceMean", inputs=["features"], outputs=["mean_score"], axes=[1], keepdims=1)
    graph = helper.make_graph([reduce_node], "IsolationForestSurrogate", [input_info], [output_info], value_info=[
        helper.make_tensor_value_info("mean_score", TensorProto.FLOAT, [None, 1])
    ])
    model_onnx = helper.make_model(graph, producer_name="zero-trust-isolation-forest")
    model_onnx.graph.output[0].name = "mean_score"
    os.makedirs("exported", exist_ok=True)
    onnx.save(model_onnx, "exported/isolation_forest.onnx")
    print("Saved exported/isolation_forest.onnx")


if __name__ == "__main__":
    main()
