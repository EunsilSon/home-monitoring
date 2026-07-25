# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소에서 작업할 때 참고할 가이드입니다.

## 프로젝트 개요

풀스택 IoT 홈 모니터링 시스템: DHT22 센서 → Arduino → Python 수집기 → Spring Boot API → PostgreSQL → Vue 3 대시보드. Docker Compose와 GitHub Actions를 통해 AWS Lightsail에 배포됩니다.

## 명령어

### 백엔드 (Spring Boot — `server/home-monitoring-server/`)

```bash
./gradlew bootRun              # 개발 서버 실행 (포트 9000, 프로파일: local)
./gradlew test                 # 전체 테스트 실행
./gradlew build                # 테스트를 포함한 전체 빌드
./gradlew clean bootJar        # 배포용 fat JAR 빌드
```

### 프론트엔드 (Vue 3 — `web/`)

```bash
npm install                    # 최초 의존성 설치
npm run dev                    # Vite 개발 서버 실행
npm run build                  # 타입 체크 + 프로덕션 빌드 (vue-tsc + vite)
npm run type-check             # 타입 체크만 실행 (vue-tsc --noEmit)
npm run preview                # 프로덕션 빌드 로컬 미리보기
```

### 수집기 (Python — 프로젝트 루트)

```bash
python collector.py            # 시리얼 데이터 읽기 + API 전송 시작
```

수집기 실행에 필요한 환경 변수: `SERIAL_PORT`, `BAUD_RATE`, `API_BASE_URL`, `DEVICE_ID`.

## 아키텍처

### 데이터 흐름

```
DHT22 → Arduino (JSON, 10초 간격) → Serial → collector.py (버퍼링, 60초마다 POST)
→ /api/sensor/bulk → Spring Boot → PostgreSQL (sensor_data + sensor_latest upsert)
→ /api/sensor/{id}/latest (60초 간격 폴링) + /api/device/{id}/status → Vue 대시보드
```

### 센서 데이터 이중 테이블 구조

`sensor_data`는 전체 이력을 저장하는 추가 전용(append-only) 테이블입니다. `sensor_latest`는 `SensorLatestRepository`의 네이티브 PostgreSQL `ON CONFLICT ... DO UPDATE` upsert 쿼리를 통해 장치별 최신 값만 유지합니다. 이를 통해 대시보드 폴링 시 매번 전체 테이블 스캔을 방지합니다. `sensor_data` 일괄 저장에는 Hibernate 배치(`batch_size: 30`)를 사용합니다.

### 장치 상태 추적

`DeviceServiceImpl`은 `device.lastSeenAt`을 180초 임계값과 비교하여 ON/OFF/UNKNOWN 상태를 판별합니다. 수집기가 벌크 저장 시마다 `recordSensorDataReceived()`를 호출하여 `lastSeenAt`을 갱신합니다. 프론트엔드도 동일한 180초 임계값(`SENSOR_STALE_THRESHOLD_MS`)을 사용하여 독립적으로 오프라인 여부를 표시합니다.

### 프론트엔드 폴링 컴포저블

`useSensorPolling.ts`는 데이터 조회의 핵심 로직입니다. `Promise.allSettled`를 사용하여 `/api/sensor/{deviceId}/latest`와 `/api/device/{deviceId}/status`를 병렬로 요청한 후 다음 요청을 스케줄링합니다. 카운트다운 타이머(`countdownRatio`, 1→0)로 프로그레스 바를 구동합니다. 센서 최신성과 장치 상태를 조합하여 `connectionStatus`를 계산합니다 (success → offline → error → unknown).

### 기본 장치 ID

`220aa852-ee70-4105-8cf5-98c23cb5e631` — `DeviceIds.java`, `collector.py`, `web/src/constants/api.ts`에 하드코딩되어 있습니다. 현재 단일 장치 기반으로 동작합니다.

### Docker 배포 구성

공유 `home-monitoring` 네트워크 위에서 세 개의 컨테이너가 실행됩니다:
- **postgres**: 헬스 체크가 포함된 PostgreSQL 15
- **java-server**: 80번 포트에서 동작하는 Spring Boot (`/api/*` 제공)
- **vue-web**: Vue 정적 빌드를 서빙하는 Nginx, `/api/` 요청을 `java-server:80`으로 프록시

Nginx는 Docker 서비스 이름으로 라우팅하므로 런타임에 IP를 하드코딩할 필요가 없습니다.

### CI/CD (GitHub Actions)

`main` 브랜치 푸시 시 파일 경로에 따라 트리거되는 두 개의 개별 워크플로우:
- `deploy-server.yml`: Gradle로 bootJar 빌드 → JAR + Dockerfile + compose 파일을 SCP 전송 → Lightsail 호스트에서 `docker compose up -d --build java-server` 실행
- `deploy-web.yml`: `npm ci && npm run build` 실행 → dist + nginx.conf + Dockerfile을 SCP 전송 → `docker compose up -d --build --no-deps vue-web` 실행

두 워크플로우 모두 `concurrency: group: home-monitoring-deploy`로 설정되어 있어 병렬 배포를 방지합니다. 또한 postgres가 healthy 상태가 될 때까지 대기한 후 앱 컨테이너를 시작합니다.

### Spring 프로파일

- **local** (`application-local.yaml`): 로컬호스트 데이터소스가 하드코딩되어 있으며, 기본값으로 사용됨
- **prod** (`application-prod.yaml`): 모든 데이터소스 정보를 환경 변수(`SPRING_DATASOURCE_URL` 등)에서 읽어오며, Docker 환경에서 사용됨

활성 프로파일은 `SPRING_PROFILES_ACTIVE` 환경 변수로 설정합니다 (기본값: `local`).

## 알려진 이슈 (PROJECT_STRUCTURE.md 참고)

- `docker-compose.yml`의 빌드 컨텍스트(`./java-server`, `./vue-web`)가 compose 파일 기준 상대 경로로 되어 있어, 저장소 루트에서 실행할 경우 올바르게 해석되지 않을 수 있습니다.
- `collector.py`의 시리얼 포트 기본값이 `COM3`(Windows)로 설정되어 있습니다 — macOS/Linux에서는 `SERIAL_PORT` 환경 변수로 재정의해야 합니다 (예: `/dev/ttyUSB0`).
- `SensorServiceImplTest.java`는 컴파일 검증이 필요합니다 — 현재 머신에 Java 26이 설치되어 있지 않아 확인하지 못했습니다.
