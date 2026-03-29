import os
import numpy as np
import tensorflow as tf
import tf2onnx

VOCAB_SIZE = 50
SEQ_LEN = 20


def generate_data(samples: int = 8000):
    np.random.seed(42)
    x = np.random.randint(1, VOCAB_SIZE, (samples, SEQ_LEN), dtype=np.int32)
    repetitive = (x[:, -1] == x[:, -2]).astype(np.float32)
    risky_tokens = np.isin(x[:, -1], [3, 7, 13, 21, 34]).astype(np.float32)
    y = ((0.55 * repetitive + 0.45 * risky_tokens) > 0.4).astype(np.float32)
    return x, y


def build_model():
    inp = tf.keras.Input(shape=(SEQ_LEN,), dtype=tf.int32, name="action_sequence")
    x = tf.keras.layers.Embedding(input_dim=VOCAB_SIZE + 1, output_dim=32)(inp)
    x = tf.keras.layers.LSTM(64, return_sequences=False)(x)
    x = tf.keras.layers.Dense(32, activation="relu")(x)
    out = tf.keras.layers.Dense(1, activation="sigmoid", name="anomaly_probability")(x)
    model = tf.keras.Model(inputs=inp, outputs=out)
    model.compile(optimizer="adam", loss="binary_crossentropy", metrics=["accuracy"])
    return model


def main() -> None:
    x, y = generate_data()
    model = build_model()
    model.fit(x, y, epochs=4, batch_size=128, validation_split=0.2, verbose=2)

    os.makedirs("exported", exist_ok=True)
    spec = (tf.TensorSpec((None, SEQ_LEN), tf.int32, name="action_sequence"),)
    model_proto, _ = tf2onnx.convert.from_keras(model, input_signature=spec, opset=17)
    with open("exported/lstm_behavior.onnx", "wb") as f:
        f.write(model_proto.SerializeToString())
    print("Saved exported/lstm_behavior.onnx")


if __name__ == "__main__":
    main()
