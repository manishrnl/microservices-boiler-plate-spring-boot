# Microservices Config Server Repository

Push these files to:

```text
https://github.com/manishrnl/microservices-boiler-plate-config-server.git
```

Spring Cloud Config resolves each service by `spring.application.name`:

- `api-gateway` reads `api-gateway.yml`
- `ai-services` reads `ai-services.yml`
- `config-server` reads `config-server.yml` if it is ever configured as a config client
- `discovery-client` reads `discovery-client.yml`
- `notification-services` reads `notification-services.yml`
- `payment-services` reads `payment-services.yml`
- `security-services` reads `security-services.yml`

`application.yml` is shared by all services.

This config set includes day-one defaults for service discovery, gateway routing, PostgreSQL, Redis, Kafka, Ollama, Mailpit, actuator health, and Prometheus metrics. Grafana, Prometheus, Loki, Promtail, and Zipkin are configured in the main boilerplate repository under `observability/`.

To push:

```powershell
git clone https://github.com/manishrnl/microservices-boiler-plate-config-server.git
Copy-Item -Path .\config-repo\* -Destination .\microservices-boiler-plate-config-server -Recurse -Force
Set-Location .\microservices-boiler-plate-config-server
git add .
git commit -m "Add microservice configuration files"
git push origin main
```
