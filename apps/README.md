# Запуск «Тёплого дома» локально

Все сервисы MVP запускаются одной командой через Docker Compose.

## Требования

- Docker;
- Свободные порты: `5432`, `8000`, `8080`-`8083`;

## Запуск

```bash
cd apps && docker compose up -d --build --wait
```


## Сервисы

| Сервис            | Адрес                  | Что это |
|-------------------|------------------------|---------|
| gateway           | http://localhost:8000  | API Gateway (nginx), основная точка входа |
| app               | http://localhost:8080  | Монолит Smart Home (Go), датчики `/api/v1/sensors` |
| temperature-api   | http://localhost:8081  | Эмулятор датчика температуры (Python/FastAPI) |
| device-service    | http://localhost:8082  | Устройства и их свойства (Java 25, Spring Boot 4) |
| telemetry-service | http://localhost:8083  | История телеметрии (Python/FastAPI), монолит получает температуру через него |
| postgres          | localhost:5432         | БД монолита, пользователь и пароль `postgres`, база `smarthome` |

Маршруты gateway: `/api/v1/devices` → device-service, `/api/v1/telemetry` → telemetry-service,
всё остальное → монолит.

## Проверка

### Postman

Импортируйте [`smarthome-api.postman_collection.json`](smarthome-api.postman_collection.json).
