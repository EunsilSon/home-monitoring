# Home Monitoring

DHT22 온습도 센서 값을 수집해서 PostgreSQL에 저장하고, Vue 기반 웹 대시보드에서 최신 온도, 습도, 체감 온도를 확인하는 홈 IoT 모니터링 시스템입니다.

## 전체 구성

```text
home-monitoring/
├── tem_hum_sensor.ino
├── collector.py
├── README.md
├── web/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   └── src/
│       ├── App.vue
│       ├── main.ts
│       ├── views/
│       ├── components/
│       ├── composables/
│       ├── services/
│       ├── constants/
│       ├── plugins/
│       ├── types/
│       └── assets/
├── server/
│   └── home-monitoring-server/
│       ├── build.gradle.kts
│       ├── settings.gradle.kts
│       ├── gradlew
│       ├── src/main/java/com/eunsilson/homemonitoring/
│       │   ├── HomemonitoringApplication.java
│       │   ├── controller/
│       │   ├── service/
│       │   ├── repository/
│       │   └── domain/
│       ├── src/main/resources/
│       └── src/test/java/
└── deploy/
    ├── docker-compose.yml
    ├── java-server/
    └── vue-web/
```

## 데이터 흐름

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

## 센서 및 수집기

### `tem_hum_sensor.ino`

Arduino용 DHT22 센서 코드입니다.

- 센서 타입: `DHT22`
- 핀: `DHTPIN 8`
- 시리얼 속도: `9600`
- 출력 값: `temperature`, `humidity`, `heatIndex`
- 출력 형식: JSON
- 측정 간격: 2초

예상 출력 예시는 다음과 같습니다.

```json
{"temperature":24.10,"humidity":45.20,"heatIndex":24.00}
```

### `collector.py`

Arduino 시리얼 출력을 읽어 서버로 전달하는 Python 수집기입니다.

- 시리얼 포트: `COM3`
- 시리얼 속도: `9600`
- 버퍼 크기: 30개
- 전송 대상: `/api/sensor/bulk`
- `recordedAt`은 수집기가 UTC 현재 시각으로 생성합니다.

## 백엔드 서버

위치:

```text
server/home-monitoring-server/
```

Spring Boot 기반 API 서버입니다.

주요 기술 스택:

- Java 17
- Spring Boot 3.5.13
- Spring Web
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Lombok
- Gradle Kotlin DSL

### 주요 패키지

```text
com.eunsilson.homemonitoring
├── controller/
├── service/
├── repository/
└── domain/
    ├── dto/
    └── entity/
```

### API

`SensorController`는 `/api/sensor` 하위 API를 제공합니다.

```text
POST /api/sensor/bulk
GET  /api/sensor/latest
```

`POST /api/sensor/bulk`는 여러 센서 데이터를 한 번에 저장합니다.

요청 데이터 형태:

```json
[
  {
    "temperature": "24.1",
    "humidity": "45.2",
    "heatIndex": "24.0",
    "recordedAt": "2026-06-09T00:00:00Z"
  }
]
```

`GET /api/sensor/latest`는 고정된 장치 UUID의 최신 센서 값을 반환합니다.

### 데이터 모델

`SensorDataEntity`

- 테이블: `sensor_data`
- 목적: 센서 원본 이력 저장
- 주요 필드: `deviceId`, `temperature`, `humidity`, `heatIndex`, `recordedAt`

`SensorLatestEntity`

- 테이블: `sensor_latest`
- 목적: 장치별 최신 센서값 캐시
- 주요 필드: `deviceId`, `temperature`, `humidity`, `heatIndex`, `recordedAt`, `updatedAt`
- PostgreSQL `ON CONFLICT`를 사용해 최신값을 upsert합니다.

`DeviceEntity`

- 테이블: `device`
- 장치 메타데이터용 엔티티로 보입니다.
- 현재 센서 저장 로직에서는 고정 UUID를 직접 사용하고 있어, 아직 적극적으로 연결되어 있지는 않습니다.

## 프론트엔드 웹

위치:

```text
web/
```

Vue 3 기반 모니터링 대시보드입니다.

주요 기술 스택:

- Vue 3
- TypeScript
- Vite
- Vuetify
- Tailwind CSS
- Axios
- Material Design Icons

### 주요 구조

```text
web/src/
├── App.vue
├── main.ts
├── views/
│   └── MonitoringView.vue
├── components/
│   ├── common/
│   └── dashboard/
├── composables/
│   ├── useSensorPolling.ts
│   └── useDatetime.ts
├── services/
│   └── sensorService.ts
├── constants/
└── types/
```

### 화면 동작

- `MonitoringView.vue`가 대시보드 화면의 중심입니다.
- `useSensorPolling.ts`가 서버의 최신 데이터를 주기적으로 가져옵니다.
- 폴링 주기는 `POLLING_INTERVAL_MS = 30_000`으로, 30초입니다.
- `MetricGrid.vue`와 `MetricCard.vue`가 온도, 습도, 체감 온도를 카드 형태로 표시합니다.
- API 기본 URL은 `VITE_API_BASE_URL` 환경 변수로 지정할 수 있고, 기본값은 빈 문자열입니다.

## 배포 구성

위치:

```text
deploy/
```

Docker 기반 배포 구성이 있습니다.

구성 서비스:

- `postgres`: PostgreSQL 15
- `java-server`: Spring Boot API 서버
- `vue-web`: Nginx로 서빙되는 Vue 정적 웹 앱

`deploy/vue-web/nginx.conf`는 `/api/` 요청을 `http://java-server:80`으로 프록시하도록 설정되어 있습니다. 즉, 브라우저는 같은 도메인으로 접근하고 Nginx가 백엔드 API 서버로 넘기는 구조입니다.
