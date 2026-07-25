import serial
import json
import requests
import time
import os
from datetime import datetime, timezone

SERIAL_PORT = os.getenv("SERIAL_PORT", "COM3")
BAUD_RATE = int(os.getenv("BAUD_RATE", "9600"))
API_BASE_URL = os.getenv("API_BASE_URL", "http://192.168.0.13:8000")
DEVICE_ID = os.getenv("DEVICE_ID", "220aa852-ee70-4105-8cf5-98c23cb5e631")

SENSOR_API_URL = f"{API_BASE_URL}/api/sensor/bulk"
SENSOR_SEND_INTERVAL_SECONDS = int(os.getenv("SENSOR_SEND_INTERVAL_SECONDS", "60"))

API_KEY = os.getenv("API_KEY", "")

session = requests.Session()
session.headers.update({"X-API-Key": API_KEY})

ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)

buffer = []
last_sensor_sent_at = time.monotonic()

print("Collector started...")


def send_sensor_data():
    global last_sensor_sent_at

    if not buffer:
        return

    now = time.monotonic()
    if now - last_sensor_sent_at < SENSOR_SEND_INTERVAL_SECONDS:
        return

    try:
        response = session.post(SENSOR_API_URL, json=buffer, timeout=5)
        print("response status: ", response.status_code)
    except Exception as e:
        print("API send failed: ", e)
    finally:
        buffer.clear()
        last_sensor_sent_at = now


while True:
    try:
        line = ser.readline().decode(errors="ignore").strip()
        if not line:
            continue

        data = json.loads(line)

        payload = {
            "deviceId": DEVICE_ID,
            "temperature": data.get("temperature"),
            "humidity": data.get("humidity"),
            "heatIndex": data.get("heatIndex"),
            "recordedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
        }
        buffer.append(payload)
        send_sensor_data()

    except json.JSONDecodeError:
        print("JSON parse error: ", line)

    except Exception as e:
        print("Error: ", e)
    
    time.sleep(0.1)
