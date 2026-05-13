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

## Run with Docker Compose

Download/pull base images first:

```bash
docker compose pull
```

Build the custom service images:

```bash
docker compose build
```

Start everything:

```bash
docker compose up --build
```

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
