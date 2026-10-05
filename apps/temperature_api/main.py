"""Эмулятор удалённого датчика температуры."""
import random
from datetime import datetime, timezone

from fastapi import FastAPI

app = FastAPI(title="temperature-api")

LOCATION_BY_SENSOR = {"1": "Living Room", "2": "Bedroom", "3": "Kitchen"}
SENSOR_BY_LOCATION = {v: k for k, v in LOCATION_BY_SENSOR.items()}


def build_response(location: str, sensor_id: str) -> dict:
    # Если location не передан - определяем по sensor_id, и наоборот
    if not location:
        location = LOCATION_BY_SENSOR.get(sensor_id, "Unknown")
    if not sensor_id:
        sensor_id = SENSOR_BY_LOCATION.get(location, "0")

    return {
        "value": round(random.uniform(18.0, 26.0), 1),
        "unit": "°C",
        "timestamp": datetime.now(timezone.utc).isoformat(),
        "location": location,
        "status": "active",
        "sensor_id": sensor_id,
        "sensor_type": "temperature",
        "description": f"Temperature sensor in {location}",
    }


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/temperature")
def temperature_by_location(location: str = "", sensorId: str = ""):
    return build_response(location, sensorId)


@app.get("/temperature/{sensor_id}")
def temperature_by_sensor(sensor_id: str):
    return build_response("", sensor_id)
