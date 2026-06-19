import serial
import json
import requests
import time
import os
from datetime import datetime, timezone

SERIAL_PORT = os.getenv("SERIAL_PORT", "COM3")
BAUD_RATE = int(os.getenv("BAUD_RATE", "9600"))
API_BASE_URL = os.getenv("API_BASE_URL", "http://192.168.0.13:8000")

SENSOR_API_URL = f"{API_BASE_URL}/api/sensor/bulk"
HEARTBEAT_API_URL = f"{API_BASE_URL}/api/device/heartbeat"
HEARTBEAT_INTERVAL_SECONDS = int(os.getenv("HEARTBEAT_INTERVAL_SECONDS", "15"))

session = requests.Session()

ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)

buffer = []
BUFFER_SIZE = 30
last_heartbeat_at = 0

print("Collector started...")


def send_heartbeat():
    global last_heartbeat_at

    now = time.monotonic()
    if now - last_heartbeat_at < HEARTBEAT_INTERVAL_SECONDS:
        return

    try:
        response = session.post(HEARTBEAT_API_URL, timeout=5)
        print("heartbeat status: ", response.status_code)
    except Exception as e:
        print("Heartbeat send failed: ", e)
    finally:
        last_heartbeat_at = now


while True:
    send_heartbeat()

    try:
        line = ser.readline().decode(errors="ignore").strip()
        if not line:
            continue

        data = json.loads(line)

        payload = {
            "temperature": data.get("temperature"),
            "humidity": data.get("humidity"),
            "heatIndex": data.get("heatIndex"),
            "recordedAt": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z")
        }
        buffer.append(payload)

        if len(buffer) >= BUFFER_SIZE:
            try:
                response = session.post(SENSOR_API_URL, json=buffer, timeout=5)
                print("response status: ", response.status_code)
                buffer.clear()    
            except Exception as e:
               print("API send failed: ", e)

    except json.JSONDecodeError:
        print("JSON parse error: ", line)

    except Exception as e:
        print("Error: ", e)
    
    time.sleep(0.1)
