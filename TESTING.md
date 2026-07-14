# API Testing Guide

Copy-and-paste commands to test all capabilities of the Petstore API.

> **Prerequisite:** start the app first with `./gradlew bootRun` (runs on `http://localhost:8080`).

---

## 1. Happy paths

### GET a pet by ID — returns `id`, `name`, `status`

```bash
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/api/pet/10
```

### POST a new pet — returns `transactionId` (UUIDv4), `dateCreated`, `status`, `name`

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id":987654,"name":"Rex","status":"available"}'
```

### Round-trip — create a pet, then fetch it back

```bash
curl -s -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id":123456789,"name":"Firulais","status":"pending"}'

curl -s http://localhost:8080/api/pet/123456789
```

---

## 2. Error handling

### 404 — pet doesn't exist

```bash
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/api/pet/999999999123
```

### 400 — non-numeric path parameter

```bash
curl -s -w "\nHTTP %{http_code}\n" http://localhost:8080/api/pet/abc
```

### 400 — validation: missing `name` and `status`

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

### 400 — validation: blank fields

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"","status":"  "}'
```

### 400 — validation: null `id`

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"name":"Rex","status":"available"}'
```

### 400 — malformed JSON body

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d 'not-json'
```

### 400 — missing body entirely

```bash
curl -s -w "\nHTTP %{http_code}\n" -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json"
```

---

## 3. Field format checks (requires `jq`)

### `transactionId` is a valid UUIDv4

```bash
curl -s -X POST http://localhost:8080/api/pet \
  -H "Content-Type: application/json" \
  -d '{"id":987654,"name":"Rex","status":"available"}' | jq -r '.transactionId' \
  | grep -E '^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$' \
  && echo "UUIDv4 valid"
```

### Each POST generates a unique `transactionId`

```bash
for i in 1 2 3; do
  curl -s -X POST http://localhost:8080/api/pet \
    -H "Content-Type: application/json" \
    -d '{"id":987654,"name":"Rex","status":"available"}' | jq -r '.transactionId'
done
```

---

## 4. Console output

While running the requests above, the `bootRun` terminal must print **two** lines per call (service layer prints before returning the response):

```
Pet found: id=10, name=doggie, status=string                      <- System.out
2026-07-14T...  INFO ... PetService : Pet found: id=10, ...       <- SLF4J
```

---

## 5. Automated test suite

```bash
./gradlew test    # unit + web-layer tests
```

```bash
./gradlew build   # full build including tests
```

---

## Expected results summary

| Test | Expected HTTP status |
|---|---|
| GET existing pet | 200 |
| POST valid pet | 200 |
| GET nonexistent pet | 404 |
| GET non-numeric id | 400 |
| POST missing/blank/null fields | 400 |
| POST malformed or missing body | 400 |
| Upstream Petstore down | 502 |

All error responses share the standardized JSON body: `timestamp`, `status`, `error`, `message`, `path`.
