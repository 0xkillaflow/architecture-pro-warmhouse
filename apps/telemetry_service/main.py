"""Telemetry Service (MVP).

Принимает и хранит показания датчиков. Для монолита выступает фасадом
над датчиками: реализует тот же контракт, что и temperature-api
(/temperature?location=, /temperature/{sensor_id}), опрашивает датчик и
сохраняет каждое показание в историю.
"""
import os
from collections import defaultdict, deque
from datetime import datetime, timezone

import httpx
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

SENSOR_API_URL = os.getenv("SENSOR_API_URL", "http://temperature-api:8081")
HISTORY_LIMIT = 100


class Reading(BaseModel):
    sensor_id: str
    value: float
    unit: str = "°C"
    location: str = ""
    timestamp: datetime | None = None


class TelemetryRepository:
    """In-memory хранилище последних показаний по каждому датчику."""

    def __init__(self, limit: int):
        self._data = defaultdict(lambda: deque(maxlen=limit))

    def save(self, reading: Reading) -> None:
        self._data[reading.sensor_id].append(reading)

    def history(self, sensor_id: str) -> list[Reading]:
        return list(reversed(self._data.get(sensor_id, [])))


class SensorClient:
    """Синхронный опрос датчика (в MVP — эмулятор temperature-api)."""

    def __init__(self, base_url: str):
        self._client = httpx.Client(base_url=base_url, timeout=5)

    def fetch(self, path: str, params: dict | None = None) -> dict:
        try:
            resp = self._client.get(path, params=params)
            resp.raise_for_status()
        except httpx.HTTPError as e:
            raise HTTPException(status_code=502, detail=f"Sensor unavailable: {e}")
        return resp.json()


app = FastAPI(title="telemetry-service")
repository = TelemetryRepository(HISTORY_LIMIT)
sensors = SensorClient(SENSOR_API_URL)


def store(data: dict) -> dict:
    repository.save(Reading(
        sensor_id=data["sensor_id"],
        value=data["value"],
        unit=data.get("unit", ""),
        location=data.get("location", ""),
        timestamp=data.get("timestamp"),
    ))
    return data


@app.get("/health")
def health():
    return {"status": "ok"}


# Контракт, совместимый с temperature-api, — его использует монолит
@app.get("/temperature")
def temperature_by_location(location: str = ""):
    return store(sensors.fetch("/temperature", {"location": location}))


@app.get("/temperature/{sensor_id}")
def temperature_by_sensor(sensor_id: str):
    return store(sensors.fetch(f"/temperature/{sensor_id}"))


# Новый API телеметрии
@app.post("/api/v1/telemetry", status_code=201)
def ingest(reading: Reading):
    reading.timestamp = reading.timestamp or datetime.now(timezone.utc)
    repository.save(reading)
    return reading


@app.get("/api/v1/telemetry/{sensor_id}")
def history(sensor_id: str):
    return repository.history(sensor_id)
