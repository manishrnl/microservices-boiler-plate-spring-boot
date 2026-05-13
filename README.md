# Enterprise Microservices Platform

Production-shaped Spring Boot microservices boilerplate for local development with service discovery, centralized configuration, API Gateway routing, persistence, messaging, AI, and observability.

## What You Get

| Layer | Component | Local URL |
| --- | --- | --- |
| Gateway | API Gateway | `http://localhost:8080` |
| Config | Spring Cloud Config Server | `http://localhost:8888` |
| Discovery | Eureka Server | `http://localhost:8761` |
| Business | Payment Service | `http://localhost:8081` |
| Business | Notification Service | `http://localhost:8082` |
| Business | AI Service | `http://localhost:8083` |
| Business | Security Service | `http://localhost:8084` |
| Data | PostgreSQL | `localhost:5432` |
| Cache | Redis | `localhost:6379` |
| Messaging | Kafka | `localhost:9094` |
| AI Runtime | Ollama | `http://localhost:11434` |
| Email Test UI | Mailpit | `http://localhost:8025` |
| Admin | pgAdmin | `http://localhost:5050` |
| Kafka Admin | Kafka UI | `http://localhost:8090` |
| Tracing | Zipkin | `http://localhost:9411` |
| Metrics | Prometheus | `http://localhost:9090` |
| Logs/Dashboards | Grafana | `http://localhost:3000` |
| Logs | Loki | `http://localhost:3100` |

## Prerequisites

- Docker Desktop
- PowerShell or a compatible terminal
- Internet access for the first image/model pull
- Optional for local Maven builds: Java 26 and Maven 3.9+

The Docker build uses Java 26 inside the Maven builder image, so you can run the platform even if your host Java version is older.



Default `.env` values are enough to run the full platform locally. No paid API keys are required.

### Required Local Values

| Variable | Purpose | Default |
| --- | --- | --- |
| `CONFIG_GIT_URI` | Config repository URL used by Config Server | public GitHub config repo |
| `CONFIG_GIT_DEFAULT_LABEL` | Config repo branch | `main` |
| `POSTGRES_DB` | Database name | `microservices_db` |
| `POSTGRES_USER` | Database user | `microservice_user` |
| `POSTGRES_PASSWORD` | Database password | `microservice_password` |
| `PGADMIN_EMAIL` | pgAdmin login email | `admin@example.com` |
| `PGADMIN_PASSWORD` | pgAdmin login password | `admin` |
| `CONFIG_SERVER_URL` | Internal Config Server URL for containers | `http://config-server:8888` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Internal Eureka URL for containers | `http://discovery-client:8761/eureka/` |
| `SPRING_DATASOURCE_URL` | Internal JDBC URL | `jdbc:postgresql://postgres:5432/microservices_db` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Internal Kafka bootstrap address | `kafka:9092` |
| `PAYMENT_EVENTS_TOPIC` | Kafka topic for payment events | `payment-events` |
| `OLLAMA_BASE_URL` | Internal Ollama URL | `http://ollama:11434` |
| `OLLAMA_CHAT_MODEL` | AI model name | `llama3.2` |
| `MAIL_HOST` | Local SMTP test host | `mailpit` |
| `MAIL_PORT` | Local SMTP test port | `1025` |
| `GRAFANA_ADMIN_USER` | Grafana login user | `admin` |
| `GRAFANA_ADMIN_PASSWORD` | Grafana login password | `admin` |

### API Keys And Secrets

For the default local setup:

- Ollama does not require an API key.
- Mailpit does not require SMTP credentials.
- PostgreSQL, pgAdmin, and Grafana use local development credentials from `.env`.
- Config Server uses a public Git repository by default.

Optional production-style additions:

- If you make the config repository private, add Git credentials through environment variables or mount a secure credential helper. Do not commit tokens.
- If you replace Mailpit with a real SMTP provider, add SMTP username/password variables and update `notification-services` configuration.
- If you replace Ollama with a cloud model provider, add that provider's API key to `.env` and update `ai-services` dependencies/configuration.

Never commit real secrets. Keep `.env` local.

## Start The Platform

Pull base images:

```powershell
docker compose pull
```

Build and start everything:

```powershell
docker compose up -d --build
```

Watch startup logs:

```powershell
docker compose logs -f config-server discovery-client api-gateway
```

Check container status:

```powershell
docker compose ps
```

First startup can take a few minutes. Config Server and Spring Boot services may log temporary `Connection refused` messages while dependencies are still starting. That is normal if the containers later become `Up` and health checks return `UP`.

## Pull The AI Model

The `ollama/ollama:latest` image provides the Ollama runtime. The model files are downloaded separately into the `ollama-data` Docker volume.

Pull the model once:

```powershell
docker compose exec ollama ollama pull llama3.2
```

Verify installed models:

```powershell
docker compose exec ollama ollama list
```

You only need to pull again if you delete the `ollama-data` volume or change `OLLAMA_CHAT_MODEL`.

## Verify The Platform

Full verification commands live in:

```text
VERIFY-API.md
```

Quick health check:

```powershell
curl.exe http://localhost:8888/actuator/health
curl.exe http://localhost:8761/actuator/health
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8081/actuator/health
curl.exe http://localhost:8082/actuator/health
curl.exe http://localhost:8083/actuator/health
curl.exe http://localhost:8084/actuator/health
```

Eureka should show five registered clients:

```powershell
curl.exe -H "Accept: application/json" http://localhost:8761/eureka/apps
```

Expected Eureka clients:

- `API-GATEWAY`
- `PAYMENT-SERVICES`
- `NOTIFICATION-SERVICES`
- `AI-SERVICES`
- `SECURITY-SERVICES`

`config-server` and `discovery-client` are running Spring services, but they are not expected to appear as Eureka clients.

## API Smoke Tests

All application APIs should be tested through the gateway on port `8080`.

Payment:

```powershell
$payment = curl.exe -sS -X POST "http://localhost:8080/api/payments" `
  -H "Content-Type: application/json" `
  --data-raw '{\"amount\":250.00,\"currency\":\"INR\",\"customerEmail\":\"user@example.com\",\"description\":\"Demo payment\"}' | ConvertFrom-Json

curl.exe http://localhost:8080/api/payments
curl.exe "http://localhost:8080/api/payments/$($payment.id)"
```

Notifications:

```powershell
Start-Sleep -Seconds 5
curl.exe http://localhost:8080/api/notifications
```

AI:

```powershell
curl.exe -X POST "http://localhost:8080/api/ai/chat" `
  -H "Content-Type: application/json" `
  --data-raw '{\"prompt\":\"Write a short project summary.\"}'
```

Security signup/login:

```powershell
$email = "user$(Get-Random)@example.com"

$auth = curl.exe -sS -X POST "http://localhost:8080/api/auth/signup" `
  -H "Content-Type: application/json" `
  --data-raw "{`"fullName`":`"Demo User`",`"email`":`"$email`",`"password`":`"password123`",`"deviceName`":`"Windows PowerShell`"}" | ConvertFrom-Json

$login = curl.exe -sS -X POST "http://localhost:8080/api/auth/login" `
  -H "Content-Type: application/json" `
  --data-raw "{`"email`":`"$email`",`"password`":`"password123`",`"deviceName`":`"Second Device`"}" | ConvertFrom-Json

curl.exe "http://localhost:8080/api/auth/sessions" `
  -H "Authorization: Bearer $($login.accessToken)"
```

## Observability

Grafana is preconfigured with:

- Prometheus metrics from service `/actuator/prometheus` endpoints
- Loki logs from Docker containers
- Zipkin traces

Open Grafana:

```text
http://localhost:3000
```

Default local login:

```text
admin / admin
```

Useful dashboards and tools:

```text
Prometheus: http://localhost:9090
Grafana:    http://localhost:3000
Loki:       http://localhost:3100
Zipkin:     http://localhost:9411
Kafka UI:   http://localhost:8090
Mailpit:    http://localhost:8025
pgAdmin:    http://localhost:5050
```

## Development Workflow

Rebuild one service:

```powershell
docker compose build api-gateway
docker compose up -d api-gateway
```

Rebuild all Spring services:

```powershell
docker compose build config-server discovery-client api-gateway payment-services notification-services ai-services security-services
docker compose up -d
```

Local Maven build, only if Java 26 is installed:

```powershell
mvn clean verify
```

Docker-based package build:

```powershell
docker compose build
```

## Troubleshooting

Show logs for all services:

```powershell
docker compose logs --tail=200
```

Show logs for one service:

```powershell
docker compose logs --tail=200 api-gateway
docker compose logs --tail=200 payment-services
docker compose logs --tail=200 notification-services
docker compose logs --tail=200 ai-services
docker compose logs --tail=200 security-services
```

Search for failures:

```powershell
docker compose logs --tail=500 | Select-String -Pattern "ERROR|Exception|Application run failed|Connection refused"
```

Restart a service:

```powershell
docker compose restart api-gateway
```

Stop everything:

```powershell
docker compose down
```

Stop and delete local data volumes:

```powershell
docker compose down -v
```

Use `down -v` carefully. It deletes PostgreSQL, Redis, Ollama model data, Grafana data, and other local volumes.

## Notes

- The API Gateway is the public entry point for application APIs.
- Eureka shows only registered client services, not every container.
- The local config files include fallbacks so services can still start if Config Server is slow during boot.
- The default setup is intended for local development, demos, and boilerplate extension.
