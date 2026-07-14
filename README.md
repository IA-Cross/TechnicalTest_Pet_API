# Petstore API

Spring Boot REST API that consumes the [Swagger Petstore API](https://petstore.swagger.io/v2) to manage pets.

## Tech Stack

Java 17, Spring Boot 3.2.7, Gradle, Spring Web (RestTemplate)

## Project Structure

```
config/       → Bean configuration (RestTemplate)
controller/   → REST endpoints
service/      → Business logic
client/       → External API calls
model/        → DTOs
```

## Endpoints

### GET /api/pet/{petId}

Fetches a pet by ID from the external Petstore API.

```bash
curl http://localhost:8080/api/pet/1
```

Response: `id`, `name`, `status`

### POST /api/pet

Creates a pet via the external Petstore API and enriches the response.

```bash
curl -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id": 12345, "name": "Buddy", "status": "available"}'
```

Response: `transactionId` (UUIDv4), `dateCreated` (system date), `status`, `name`

## Run

```bash
./gradlew clean build
./gradlew bootRun
```

App starts on `http://localhost:8080`.