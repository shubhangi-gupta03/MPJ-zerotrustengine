# Self-Learning Zero Trust Security Engine

Production-style multi-service cybersecurity platform implementing "Never Trust, Always Verify" with Java microservices, Kafka event streaming, Redis controls, MySQL/Mongo persistence, ONNX inference, and a React SOC dashboard.

## Architecture

```
                     +---------------------------+
                     |        React Frontend     |
                     |   (Vite, TS, Tailwind)    |
                     +------------+--------------+
                                  |
                                  v
                     +---------------------------+
                     |      API Gateway :8080    |
                     |  JWT filter + rate limit  |
                     +------+-----+------+-------+
                            |     |      |
        +-------------------+     |      +-------------------+
        v                         v                          v
 +--------------+          +--------------+           +--------------+
 | Auth :8081   |          | Device :8082 |           | Behavior:8083|
 +------+-------+          +------+-------+           +------+-------+
        |                         |                          |
        +-------------------------+--------------------------+
                                  |
                                  v
                          +---------------+
                          |     Kafka     |
                          +---+---+---+---+
                              |   |   |
                              v   v   v
 +---------------+     +---------------+     +----------------------+
 | Risk :8084    | --> | Policy :8085  | --> | EventStream :8086    |
 +-------+-------+     +-------+-------+     | Mongo audit + WS     |
         |                     |             +----------+-----------+
         +---------------------+                        |
                                                       v
                                              +------------------+
                                              | Monitoring :8087 |
                                              +------------------+

Data stores:
- MySQL: users/auth tables
- MongoDB: devices, baselines, anomalies, risk_scores, audit_events
- Redis: session blacklist + counters
```

## Repository Layout

`zero-trust-engine/`
- `backend/` (all Spring Boot services + shared `common-lib`)
- `frontend/` (React 18 + TypeScript dashboard)
- `ml-models/` (training/export scripts + ONNX artifacts)
- `docker-compose.yml`

## Services and Ports

- `api-gateway-service` -> `8080`
- `auth-service` -> `8081`
- `device-trust-service` -> `8082`
- `behavior-service` -> `8083`
- `risk-scoring-service` -> `8084`
- `policy-engine-service` -> `8085`
- `event-streaming-service` -> `8086`
- `monitoring-service` -> `8087`
- `eureka-server` -> `8761`

## Prerequisites

- Java 17
- Maven 3.9+
- Node.js 20+
- Python 3.10+
- Docker + Docker Compose

## Environment Variables

Use `.env` for compose values (examples):

```env
MYSQL_DATABASE=zerotrust
MYSQL_USER=zt_user
MYSQL_PASSWORD=zt_password
MYSQL_ROOT_PASSWORD=root_password
MONGODB_URI=mongodb://mongodb:27017/zerotrust
REDIS_HOST=redis
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
EUREKA_SERVER_URL=http://eureka-server:8761/eureka
JWT_PRIVATE_KEY_PATH=/run/secrets/jwt_private.pem
JWT_PUBLIC_KEY_PATH=/run/secrets/jwt_public.pem
FRONTEND_ORIGIN=http://localhost:5173
SMTP_HOST=smtp
SMTP_PORT=587
SMTP_USERNAME=
SMTP_PASSWORD=
SLACK_WEBHOOK_URL=
```

## Setup and Run

1. Build backend shared lib:
   - `cd backend`
   - `mvn -pl common-lib -am clean install`

2. Start infrastructure and services:
   - `cd ..`
   - `docker compose up --build`

3. Start frontend locally (optional outside compose):
   - `cd frontend`
   - `npm install`
   - `npm run dev`

4. Build frontend:
   - `npm run build`

## Seeded Credentials

- `admin@zerotrust.dev / Admin@123 / ROLE_ADMIN / MFA enabled`
- `alice@company.com / Test@1234 / ROLE_USER`
- `bob@company.com / Test@1234 / ROLE_USER`
- `charlie@company.com / Test@1234 / ROLE_ANALYST`
- `diana@company.com / Test@1234 / ROLE_USER`
- `eve@company.com / Test@1234 / ROLE_USER`
- `frank@company.com / Test@1234 / ROLE_MANAGER`
- `grace@company.com / Test@1234 / ROLE_USER`
- `henry@company.com / Test@1234 / ROLE_USER`
- `iris@company.com / Test@1234 / ROLE_ANALYST`

## ML Models

Scripts in `ml-models/`:
- `isolation_forest.py`
- `device_classifier.py`
- `login_anomaly.py`
- `lstm_behavior.py`

Generated artifacts in `ml-models/exported/`:
- `isolation_forest.onnx`
- `device_classifier.onnx`
- `login_anomaly.onnx`
- `lstm_behavior.onnx`

Run all exports:

```bash
cd ml-models
python isolation_forest.py
python device_classifier.py
python login_anomaly.py
python lstm_behavior.py
```

## End-to-End Test Walkthrough

1. Register/login via gateway:
   - `POST /api/auth/register`
   - `POST /api/auth/login`
2. Submit device fingerprint:
   - `POST /api/device/fingerprint`
3. Stream user behavior events:
   - `POST /api/behavior/event`
4. Verify risk recomputation:
   - `GET /api/risk/score/{sessionId}`
5. Evaluate policy decision:
   - `POST /api/policy/evaluate`
6. Observe alerts:
   - WebSocket subscribe on `/ws/events` -> `/topic/alerts`
7. Review SOC metrics:
   - `GET /api/monitor/stats`
   - `GET /api/monitor/threat-feed`

## Validation Status

- Frontend TypeScript build: passed (`npm run build`)
- Python model exports: passed (all `.onnx` files generated)
- Backend compile/runtime verification depends on Maven availability in environment:
  - run per service: `mvn spring-boot:run`
  - run aggregated build: `mvn clean package`
