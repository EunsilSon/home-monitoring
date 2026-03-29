import serial
import json
import requests
import time
from datetime import datetime, timezone

SERIAL_PORT = "COM3"
BAUD_RATE = 9600

API_URL = "http://192.168.0.13:8000/sensor/bulk"

session = requests.Session()

ser = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)

buffer = []
BUFFER_SIZE = 30

print("Collector started...")

while True:
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
                response = session.post(API_URL, json=buffer, timeout=5)
                print("response status: ", response.status_code)
                buffer.clear()    
            except Exception as e:
               print("API send failed: ", e)

    except json.JSONDecodeError:
        print("JSON parse error: ", line)

    except Exception as e:
        print("Error: ", e)
    
    time.sleep(0.1)