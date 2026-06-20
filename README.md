# Home Monitoring

DHT22 온습도 센서 값을 수집해서 PostgreSQL에 저장하고, Vue 기반 웹 대시보드에서 최신 온도, 습도, 체감 온도를 확인하는 홈 IoT 모니터링 시스템입니다.

<br>

### 구축 과정
홈 모니터링 시스템 구축 과정을 아래 블로그에서 확인할 수 있습니다.  
- [Chapter 1. 아두이노부터 클라우드 배포까지 설계와 기획](https://velog.io/@eunsilson/%ED%99%88-IoT-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%95%84%EB%91%90%EC%9D%B4%EB%85%B8%EB%B6%80%ED%84%B0-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C-%EB%B0%B0%ED%8F%AC%EA%B9%8C%EC%A7%80-%EC%84%A4%EA%B3%84%EC%99%80-%EA%B8%B0%ED%9A%8D)

<br>

### 데이터 흐름

```text
DHT22 센서
  ↓
Arduino 스케치: tem_hum_sensor.ino
  ↓ Serial JSON
Python 수집기: collector.py
  ↓ POST /api/sensor/bulk
Spring Boot 서버
  ↓
PostgreSQL
  ↓ GET /api/sensor/latest
Vue 웹 대시보드
```

1. `tem_hum_sensor.ino`가 DHT22 센서에서 온도, 습도, 체감 온도를 읽습니다.
2. Arduino가 2초마다 시리얼 포트로 JSON 데이터를 출력합니다.
3. `collector.py`가 시리얼 데이터를 읽고 30개씩 버퍼링합니다.
4. 버퍼가 차면 Spring Boot 서버의 `/api/sensor/bulk`로 일괄 전송합니다.
5. 서버는 원본 센서 이력을 `sensor_data`에 저장하고 최신값을 `sensor_latest`에 upsert합니다.
6. Vue 웹 앱은 `/api/sensor/latest`를 30초 간격으로 조회해 대시보드를 갱신합니다.

<br>

### 데이터 모델

`SensorDataEntity`
- 목적: 센서 원본 이력 저장

`SensorLatestEntity`
- 목적: 장치별 최신 센서값 캐시
- PostgreSQL `ON CONFLICT`를 사용해 최신값 upsert

`DeviceEntity`
- 목적: 장치 메타데이터
