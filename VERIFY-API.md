# API Verification Guide

Run these commands from the repository root after starting the stack:

```powershell
docker compose up -d --build
docker compose ps
```

## 1. Spring Service Health

All seven Spring services should return `UP`.

```powershell
curl.exe http://localhost:8888/actuator/health
curl.exe http://localhost:8761/actuator/health
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8081/actuator/health
curl.exe http://localhost:8082/actuator/health
curl.exe http://localhost:8083/actuator/health
curl.exe http://localhost:8084/actuator/health
```

Service map:

| Service | Port | Direct health URL |
| --- | ---: | --- |
| Config Server | 8888 | `http://localhost:8888/actuator/health` |
| Eureka Discovery | 8761 | `http://localhost:8761/actuator/health` |
| API Gateway | 8080 | `http://localhost:8080/actuator/health` |
| Payment Service | 8081 | `http://localhost:8081/actuator/health` |
| Notification Service | 8082 | `http://localhost:8082/actuator/health` |
| AI Service | 8083 | `http://localhost:8083/actuator/health` |
| Security Service | 8084 | `http://localhost:8084/actuator/health` |

## 2. Eureka Discovery

Eureka should show the five client services: `API-GATEWAY`, `PAYMENT-SERVICES`, `NOTIFICATION-SERVICES`, `AI-SERVICES`, and `SECURITY-SERVICES`.

`config-server` and `discovery-client` are not expected to appear in Eureka.

```powershell
curl.exe -H "Accept: application/json" http://localhost:8761/eureka/apps
```

Open the dashboard:

```text
http://localhost:8761
```

## 3. Config Server

Verify Config Server can serve service configuration.

```powershell
curl.exe http://localhost:8888/api-gateway/default
curl.exe http://localhost:8888/payment-services/default
curl.exe http://localhost:8888/notification-services/default
curl.exe http://localhost:8888/ai-services/default
curl.exe http://localhost:8888/security-services/default
curl.exe http://localhost:8888/discovery-client/default
```

## 4. Gateway Route Health

These go through the API Gateway on port `8080`.

```powershell
curl.exe http://localhost:8080/api/payments/health
curl.exe http://localhost:8080/api/notifications/health
curl.exe http://localhost:8080/api/ai/health
curl.exe http://localhost:8080/api/auth/health
```

## 5. Payment API

Create a payment:

```powershell
$payment = curl.exe -sS -X POST "http://localhost:8080/api/payments" `
  -H "Content-Type: application/json" `
  --data-raw '{\"amount\":250.00,\"currency\":\"INR\",\"customerEmail\":\"user@example.com\",\"description\":\"Demo payment\"}' | ConvertFrom-Json

$payment
```

List payments:

```powershell
curl.exe http://localhost:8080/api/payments
```

Get one payment by ID:

```powershell
curl.exe "http://localhost:8080/api/payments/$($payment.id)"
```

## 6. Notification API

Payment creation publishes a Kafka event. After a few seconds, list notifications:

```powershell
Start-Sleep -Seconds 5
curl.exe http://localhost:8080/api/notifications
```

You can also open Mailpit:

```text
http://localhost:8025
```

## 7. AI API

Pull the model once per Docker volume:

```powershell
docker compose exec ollama ollama pull llama3.2
docker compose exec ollama ollama list
```

Chat through the gateway:

```powershell
curl.exe -X POST "http://localhost:8080/api/ai/chat" `
  -H "Content-Type: application/json" `
  --data-raw '{\"prompt\":\"hello\"}'
```

Expected response includes `answer`, `model`, and `createdAt`.

## 8. Security API

Use a unique email if you run this more than once.

```powershell
$email = "user$(Get-Random)@example.com"

$auth = curl.exe -sS -X POST "http://localhost:8080/api/auth/signup" `
  -H "Content-Type: application/json" `
  --data-raw "{`"fullName`":`"Demo User`",`"email`":`"$email`",`"password`":`"password123`",`"deviceName`":`"Windows PowerShell`"}" | ConvertFrom-Json

$auth
```

Login:

```powershell
$login = curl.exe -sS -X POST "http://localhost:8080/api/auth/login" `
  -H "Content-Type: application/json" `
  --data-raw "{`"email`":`"$email`",`"password`":`"password123`",`"deviceName`":`"Second Device`"}" | ConvertFrom-Json

$login
```

List sessions:

```powershell
curl.exe "http://localhost:8080/api/auth/sessions" `
  -H "Authorization: Bearer $($login.accessToken)"
```

Logout current session:

```powershell
curl.exe -X DELETE "http://localhost:8080/api/auth/sessions/current" `
  -H "Authorization: Bearer $($login.accessToken)"
```

## 9. Supporting Tools

```text
pgAdmin:     http://localhost:5050
Kafka UI:   http://localhost:8090
Mailpit:    http://localhost:8025
Zipkin:     http://localhost:9411
Prometheus: http://localhost:9090
Grafana:    http://localhost:3000
Loki:       http://localhost:3100
Ollama:     http://localhost:11434
```

## 10. Logs And Troubleshooting

Show all logs:

```powershell
docker compose logs --tail=200
```

Show one service:

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
