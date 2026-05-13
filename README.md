# Java Spring Boot Microservices Boilerplate

Reusable Java 26 / Spring Boot 4 boilerplate with:

- `api-gateway` on port `8080`
- `config-server` on port `8888`
- `discovery-client` Eureka server on port `8761`
- `payment-services` on port `8081`
- `notification-services` on port `8082`
- `ai-services` on port `8083`
- `security-services` on port `8084`
- PostgreSQL, Redis, Kafka, and Ollama in Docker Compose
- pgAdmin, Kafka UI, Mailpit, Zipkin, Prometheus, Loki, Promtail, and Grafana for local development

## Run with Docker Compose

Create local environment values:

```bash
cp .env.example .env
```

Download/pull base images first:

```bash
docker compose pull
```

Start everything:

```bash
docker compose up --build
```

## Local URLs

- API Gateway: `http://localhost:8080`
- Config Server: `http://localhost:8888`
- Eureka Dashboard: `http://localhost:8761`
- pgAdmin: `http://localhost:5050`
- Kafka UI: `http://localhost:8090`
- Mailpit: `http://localhost:8025`
- Zipkin: `http://localhost:9411`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Loki: `http://localhost:3100`
- Ollama: `http://localhost:11434`

## Observability

Grafana is preconfigured with:

- Prometheus for service metrics from `/actuator/prometheus`
- Loki for Docker container logs and error search
- Zipkin for request traces

Open Grafana at `http://localhost:3000`. The default local login is `admin` / `admin`.

The config server reads from:

```text
https://github.com/manishrnl/microservices-boiler-plate-config-server.git
```

For the AI service, pull the configured Ollama model once:

```bash
docker compose exec ollama ollama pull llama3.2
```

## Example Requests

Create a payment through the gateway:

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d "{\"amount\": 250.00, \"currency\": \"INR\", \"customerEmail\": \"user@example.com\", \"description\": \"Demo payment\"}"
```

List notifications:

```bash
curl http://localhost:8080/api/notifications
```

Chat with the AI service:

```bash
curl -X POST http://localhost:8080/api/ai/chat \
  -H "Content-Type: application/json" \
  -d "{\"prompt\": \"Write a short project summary for this microservices boilerplate.\"}"
```

Signup through the security service:

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"fullName\": \"Demo User\", \"email\": \"user@example.com\", \"password\": \"password123\", \"deviceName\": \"Chrome on Windows\"}"
```

Login from another device:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"user@example.com\", \"password\": \"password123\", \"deviceName\": \"Mobile App\"}"
```

Each user can keep at most 5 active sessions. When a sixth device logs in, the oldest session is deleted automatically.

## Local Maven Build

```bash
mvn clean verify
```

If your local machine does not have Java 26 installed, use Docker Compose to build with the Java 26 Maven image.
