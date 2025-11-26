# Last Mile Driver Service

Early Express 물류 플랫폼의 **최종 배송 담당자 관리 서비스**입니다. 허브에서 최종 목적지까지의 배송을 담당하는 드라이버를 관리하며, 자동 배정 알고리즘을 통해 효율적인 배송 인력 운영을 지원합니다.

---

## 📋 목차

1. [개요](#개요)
2. [기술 스택](#기술-스택)
3. [아키텍처](#아키텍처)
4. [도메인 모델](#도메인-모델)
5. [API 엔드포인트](#api-엔드포인트)
6. [서비스 연동](#서비스-연동)
7. [환경 설정](#환경-설정)
8. [실행 방법](#실행-방법)
9. [프로젝트 구조](#프로젝트-구조)

---

## 개요

Last Mile Driver Service는 최종 배송 단계(Last Mile Delivery)를 담당하는 드라이버를 관리하는 서비스입니다.

### 주요 기능

- **드라이버 관리**: 최종 배송 담당자 등록, 상태 관리, 근무 관리
- **자동 배정**: 허브별 우선순위 기반 드라이버 자동 배정
- **배송 통계**: 총 배송 건수, 평균 배송 시간 등 성과 지표 관리
- **역할별 조회**: 마스터/허브 관리자별 접근 권한 분리

### Hub Driver vs Last Mile Driver

| 구분 | Hub Driver | Last Mile Driver |
|------|------------|------------------|
| **담당 구간** | 허브 간 배송 | 허브 → 최종 목적지 |
| **소속** | 허브 경로 기반 | 특정 허브 소속 |
| **배정 방식** | 경로 기반 자동 배정 | 허브 내 우선순위 기반 |
| **통계** | 허브 간 이동 시간 | 최종 배송 시간 |

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| **Framework** | Spring Boot 3.x |
| **Language** | Java 21 |
| **Database** | PostgreSQL |
| **Service Discovery** | Netflix Eureka |
| **Security** | OAuth 2.0 Resource Server (Keycloak) |
| **Build Tool** | Gradle |

---

## 아키텍처

### 시스템 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           Last Mile Driver Service                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  Presentation Layer                                                                  │
│  ┌───────────────────┐ ┌───────────────────┐ ┌───────────────────┐                  │
│  │ MasterController  │ │ HubManager        │ │ Internal          │                  │
│  │ /web/master       │ │ Controller        │ │ Controller        │                  │
│  │                   │ │ /web/hub-manager  │ │ /internal         │                  │
│  │ • 전체 조회       │ │ • 허브별 조회     │ │ • 생성/배정/완료  │                  │
│  └───────────────────┘ └───────────────────┘ └───────────────────┘                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  Application Layer                                                                   │
│  ┌──────────────────────────────┐  ┌──────────────────────────────┐                 │
│  │  LastMileDriverCommandService│  │  LastMileDriverQueryService  │                 │
│  │  • create()                  │  │  • findAll()                 │                 │
│  │  • assignDelivery()          │  │  • findByHubId()             │                 │
│  │  • completeDelivery()        │  │  • findByHubIdAndStatus()    │                 │
│  │  • cancelDelivery()          │  │                              │                 │
│  └──────────────────────────────┘  └──────────────────────────────┘                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  Domain Layer                                                                        │
│  ┌─────────────────────────────────────────────────────────────────────────────┐    │
│  │  LastMileDriver (Aggregate Root)                                            │    │
│  │  ┌─────────────────┐  ┌─────────────────────┐                               │    │
│  │  │ LastMileDriverId│  │ LastMileDriverStatus│                               │    │
│  │  │ (Value Object)  │  │ (Enum)              │                               │    │
│  │  └─────────────────┘  └─────────────────────┘                               │    │
│  └─────────────────────────────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  Infrastructure Layer                                                                │
│  ┌────────────────────────────┐                                                     │
│  │       PostgreSQL           │                                                     │
│  │       Repository           │                                                     │
│  └────────────────────────────┘                                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 서비스 연동 구조

```
┌─────────────────┐                    ┌─────────────────────────────┐
│   User Service  │                    │  Last Mile Driver Service   │
│                 │───── Feign ───────▶│                             │
│ (드라이버 등록)  │  POST /internal/   │  • 드라이버 생성            │
│                 │  drivers           │  • 상태: AVAILABLE          │
└─────────────────┘                    └─────────────────────────────┘
                                                     ▲
                                                     │
                                                     │ Feign
                                                     │
┌─────────────────────────────────────────────────────────────────────┐
│                    Last Mile Delivery Service                        │
│                                                                      │
│  배송 생성 시:                                                        │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ POST /internal/drivers/assign                                │    │
│  │ → 허브별 우선순위가 가장 낮은 드라이버 자동 배정              │    │
│  │ → 드라이버 상태: AVAILABLE → ON_DELIVERY                     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  배송 완료 시:                                                        │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ PUT /internal/drivers/{driverId}/complete                    │    │
│  │ → 통계 업데이트 (totalDeliveries, averageDeliveryTimeMin)    │    │
│  │ → 드라이버 상태: ON_DELIVERY → AVAILABLE                     │    │
│  └─────────────────────────────────────────────────────────────┘    │
│                                                                      │
│  배송 취소 시:                                                        │
│  ┌─────────────────────────────────────────────────────────────┐    │
│  │ PUT /internal/drivers/{driverId}/cancel                      │    │
│  │ → 배정 해제, 드라이버 상태: ON_DELIVERY → AVAILABLE          │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 도메인 모델

### LastMileDriver (Aggregate Root)

```java
public class LastMileDriver {
    // 식별자
    private LastMileDriverId id;          // UUID
    
    // 기본 정보
    private String userId;                // 사용자 ID (User Service 연동)
    private String hubId;                 // 소속 허브 ID
    private String name;                  // 드라이버 이름
    
    // 상태 정보
    private LastMileDriverStatus status;  // 드라이버 상태
    private String currentDeliveryId;     // 현재 배송 ID (배송 중일 때)
    
    // 배정 관련
    private Integer assignmentPriority;   // 배정 우선순위 (낮을수록 우선 배정)
    private LocalDateTime availableFrom;  // 배정 가능 시점
    
    // 통계
    private Long totalDeliveries;         // 총 배송 건수
    private Long totalDeliveryTimeMin;    // 총 배송 시간 (분)
    private Long averageDeliveryTimeMin;  // 평균 배송 시간 (분)
    private LocalDateTime lastDeliveryCompletedAt;  // 마지막 배송 완료 시간
    
    // Audit
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private LocalDateTime deletedAt;
    private String deletedBy;
    private boolean isDeleted;
}
```

### Value Objects

| Value Object | 설명 | 주요 필드 |
|--------------|------|----------|
| `LastMileDriverId` | 드라이버 고유 ID | UUID 기반 value |

### LastMileDriverStatus (드라이버 상태)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           드라이버 상태 흐름도                                        │
└─────────────────────────────────────────────────────────────────────────────────────┘

[드라이버 생성]
      │
      ▼
┌─────────────┐    assignDelivery()    ┌─────────────┐
│  AVAILABLE  │───────────────────────▶│ ON_DELIVERY │
│  배정 가능   │◀───────────────────────│  배송 중     │
└─────────────┘  completeDelivery()    └─────────────┘
      │           cancelDelivery()            │
      │                                       │
      │ endWork()                             │ (배송 중에는 불가)
      ▼                                       │
┌─────────────┐                               │
│  OFF_DUTY   │                               │
│  비활성      │                               │
└─────────────┘                               │
      │                                       │
      │ deactivate()                          │
      ▼                                       │
┌─────────────┐                               │
│  INACTIVE   │◀──────────────────────────────┘
│  휴직        │   (배송 중에는 휴직 불가)
└─────────────┘
      │
      │ activate()
      ▼
┌─────────────┐
│  AVAILABLE  │
└─────────────┘
```

| 상태 | 설명 | 배정 가능 | 근무 중 |
|------|------|----------|---------|
| `AVAILABLE` | 배정 가능 | ✅ | ✅ |
| `ON_DELIVERY` | 배송 중 | ❌ | ✅ |
| `OFF_DUTY` | 비활성 (퇴근) | ❌ | ❌ |
| `INACTIVE` | 휴직 | ❌ | ❌ |

### 비즈니스 규칙

1. **배송 배정**: `AVAILABLE` 상태에서만 배정 가능
2. **배송 완료/취소**: `ON_DELIVERY` 상태에서만 가능
3. **근무 종료**: `ON_DELIVERY` 상태에서는 근무 종료 불가
4. **휴직 처리**: `ON_DELIVERY` 상태에서는 휴직 불가
5. **삭제**: `ON_DELIVERY` 상태에서는 삭제 불가

---

## API 엔드포인트

### Internal API (내부 서비스 간 통신)

**Base Path**: `/v1/last-mile-driver/internal`

> ⚠️ 이 API는 내부 서비스 간 통신용입니다. Gateway를 통해 외부에 노출되지 않습니다.

| Method | Endpoint | 설명 | 호출 서비스 |
|--------|----------|------|-------------|
| `POST` | `/drivers` | 드라이버 생성 | User Service |
| `POST` | `/drivers/assign` | 드라이버 자동 배정 | LastMileDelivery Service |
| `PUT` | `/drivers/{driverId}/complete` | 배송 완료 통지 | LastMileDelivery Service |
| `PUT` | `/drivers/{driverId}/cancel` | 배송 취소 통지 | LastMileDelivery Service |

#### 드라이버 생성

User Service에서 최종 배송 담당자 등록 시 호출됩니다.

```http
POST /v1/last-mile-driver/internal/drivers
X-User-Id: admin-001
Content-Type: application/json
```

**Request**
```json
{
  "userId": "user-driver-001",
  "hubId": "hub-seoul-001",
  "name": "김배송"
}
```

**Response (200 OK)**
```json
{
  "driverId": "driver-uuid-001",
  "userId": "user-driver-001",
  "hubId": "hub-seoul-001",
  "name": "김배송",
  "status": "AVAILABLE"
}
```

---

#### 드라이버 자동 배정

LastMileDelivery 생성 시 해당 허브의 가용 드라이버 중 우선순위가 가장 낮은 드라이버를 자동 배정합니다.

```http
POST /v1/last-mile-driver/internal/drivers/assign
Content-Type: application/json
```

**Request**
```json
{
  "hubId": "hub-seoul-001",
  "deliveryId": "last-mile-delivery-001"
}
```

**Response (200 OK)**
```json
{
  "driverId": "driver-uuid-001",
  "userId": "user-driver-001",
  "hubId": "hub-seoul-001",
  "driverName": "김배송",
  "status": "ON_DELIVERY",
  "assignedAt": "2025-01-15T10:30:00"
}
```

**배정 알고리즘**:
1. 해당 허브 소속 드라이버 중 `AVAILABLE` 상태인 드라이버 조회
2. `assignmentPriority`가 가장 낮은 드라이버 선택
3. 선택된 드라이버의 `assignmentPriority` 증가 (Round-Robin 효과)
4. 드라이버 상태를 `ON_DELIVERY`로 변경

---

#### 배송 완료 통지

배송 완료 시 드라이버 통계를 업데이트하고 상태를 변경합니다.

```http
PUT /v1/last-mile-driver/internal/drivers/{driverId}/complete
Content-Type: application/json
```

**Request**
```json
{
  "deliveryTimeMin": 45
}
```

**Response (200 OK)**
```json
{
  "driverId": "driver-uuid-001",
  "status": "AVAILABLE",
  "message": "배송이 완료되었습니다.",
  "timestamp": 1705312200000
}
```

**처리 내용**:
- `totalDeliveries` 증가
- `totalDeliveryTimeMin`에 배송 시간 추가
- `averageDeliveryTimeMin` 재계산
- `lastDeliveryCompletedAt` 업데이트
- `currentDeliveryId` 초기화
- `assignmentPriority` 0으로 리셋
- 상태를 `AVAILABLE`로 변경

---

#### 배송 취소 통지

배송 취소 시 드라이버 배정을 해제합니다.

```http
PUT /v1/last-mile-driver/internal/drivers/{driverId}/cancel
Content-Type: application/json
```

**Request** (Optional)
```json
{
  "reason": "고객 요청으로 취소"
}
```

**Response (200 OK)**
```json
{
  "driverId": "driver-uuid-001",
  "status": "AVAILABLE",
  "message": "배송이 취소되었습니다.",
  "timestamp": 1705312200000
}
```

---

### Hub Manager API (허브 관리자용)

**Base Path**: `/v1/last-mile-driver/web/hub-manager`

| Method | Endpoint | 설명 |
|--------|----------|------|
| `GET` | `/drivers` | 허브별 드라이버 목록 조회 |

#### 허브별 드라이버 목록 조회

```http
GET /v1/last-mile-driver/web/hub-manager/drivers?hubId=hub-seoul-001&status=AVAILABLE
X-User-Id: hub-manager-001
X-User-Roles: HUB_MANAGER
```

**Query Parameters**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| `hubId` | String | ✅ | 허브 ID |
| `status` | Enum | ❌ | 드라이버 상태 필터 |
| `page` | Integer | ❌ | 페이지 번호 (기본값: 0) |
| `size` | Integer | ❌ | 페이지 크기 (기본값: 20) |
| `sort` | String | ❌ | 정렬 기준 (기본값: createdAt,DESC) |

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "driverId": "driver-uuid-001",
        "userId": "user-driver-001",
        "name": "김배송",
        "status": "AVAILABLE",
        "statusDescription": "배정 가능",
        "currentDeliveryId": null,
        "assignmentPriority": 0,
        "totalDeliveries": 150,
        "averageDeliveryTimeMin": 38,
        "availableFrom": "2025-01-15T09:00:00"
      },
      {
        "driverId": "driver-uuid-002",
        "userId": "user-driver-002",
        "name": "이배달",
        "status": "ON_DELIVERY",
        "statusDescription": "배송 중",
        "currentDeliveryId": "last-mile-delivery-002",
        "assignmentPriority": 3,
        "totalDeliveries": 200,
        "averageDeliveryTimeMin": 42,
        "availableFrom": "2025-01-15T09:00:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 20,
      "totalElements": 15,
      "totalPages": 1
    }
  }
}
```

---

### Master API (마스터 관리자용)

**Base Path**: `/v1/last-mile-driver/web/master`

| Method | Endpoint | 설명 |
|--------|----------|------|
| `GET` | `/drivers` | 전체 드라이버 목록 조회 |

#### 전체 드라이버 목록 조회

마스터 관리자는 모든 허브의 드라이버를 조회할 수 있습니다.

```http
GET /v1/last-mile-driver/web/master/drivers
X-User-Id: master-001
X-User-Roles: MASTER
```

**Response (200 OK)**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "driverId": "driver-uuid-001",
        "userId": "user-driver-001",
        "hubId": "hub-seoul-001",
        "name": "김배송",
        "status": "AVAILABLE",
        "statusDescription": "배정 가능",
        "currentDeliveryId": null,
        "assignmentPriority": 0,
        "totalDeliveries": 150,
        "totalDeliveryTimeMin": 5700,
        "averageDeliveryTimeMin": 38,
        "lastDeliveryCompletedAt": "2025-01-15T14:30:00",
        "availableFrom": "2025-01-15T09:00:00",
        "createdAt": "2024-06-01T10:00:00"
      }
    ],
    "pageInfo": {
      "page": 0,
      "size": 20,
      "totalElements": 100,
      "totalPages": 5
    }
  }
}
```

**Master vs Hub Manager 응답 차이**

| 필드 | Hub Manager | Master |
|------|-------------|--------|
| `hubId` | ❌ | ✅ |
| `totalDeliveryTimeMin` | ❌ | ✅ |
| `lastDeliveryCompletedAt` | ❌ | ✅ |
| `createdAt` | ❌ | ✅ |

---

## 서비스 연동

### 연동 서비스 목록

| 서비스 | 연동 방식 | 역할 |
|--------|----------|------|
| **User Service** | Feign (호출받음) | 드라이버 생성 요청 |
| **LastMileDelivery Service** | Feign (호출받음) | 배정/완료/취소 통지 |

### 시퀀스 다이어그램

#### 드라이버 등록 플로우

```
User Service               LastMileDriver Service
     │                              │
     │  POST /internal/drivers      │
     │  {userId, hubId, name}       │
     │─────────────────────────────▶│
     │                              │ create LastMileDriver
     │                              │ status: AVAILABLE
     │                              │ assignmentPriority: 0
     │     {driverId, status}       │
     │◀─────────────────────────────│
     │                              │
```

#### 배송 배정 플로우

```
LastMileDelivery Service       LastMileDriver Service
        │                              │
        │  POST /internal/drivers/     │
        │       assign                 │
        │  {hubId, deliveryId}         │
        │─────────────────────────────▶│
        │                              │ 1. hubId로 AVAILABLE 드라이버 조회
        │                              │ 2. assignmentPriority 오름차순 정렬
        │                              │ 3. 첫 번째 드라이버 선택
        │                              │ 4. assignDelivery() 호출
        │                              │    - status: ON_DELIVERY
        │                              │    - currentDeliveryId 설정
        │                              │    - assignmentPriority++
        │   {driverId, driverName,     │
        │    status, assignedAt}       │
        │◀─────────────────────────────│
        │                              │
```

#### 배송 완료 플로우

```
LastMileDelivery Service       LastMileDriver Service
        │                              │
        │  PUT /internal/drivers/      │
        │      {driverId}/complete     │
        │  {deliveryTimeMin: 45}       │
        │─────────────────────────────▶│
        │                              │ completeDelivery() 호출
        │                              │ - totalDeliveries++
        │                              │ - totalDeliveryTimeMin += 45
        │                              │ - averageDeliveryTimeMin 재계산
        │                              │ - status: AVAILABLE
        │                              │ - currentDeliveryId: null
        │                              │ - assignmentPriority: 0
        │   {driverId, status,         │
        │    message: "완료"}          │
        │◀─────────────────────────────│
        │                              │
```

---

## 환경 설정

### 환경 변수 (.env)

```properties
# 서버 설정
APP_PORT=4021

# 데이터베이스
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/last_mile_driver_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Eureka
EUREKA_DEFAULT_ZONE=http://localhost:8761/eureka/

# Keycloak
KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/early-express
```

### application.yml 주요 설정

```yaml
spring:
  application:
    name: last-mile-driver-service
  
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        default_schema: last_mile_driver

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_DEFAULT_ZONE}
  instance:
    prefer-ip-address: true
```

---

## 실행 방법

### 사전 요구사항

- Java 21
- PostgreSQL 15+
- Eureka Server
- Keycloak

### 로컬 실행

```bash
# 1. 데이터베이스 생성
createdb last_mile_driver_db

# 2. 환경 변수 설정
export $(cat .env | xargs)

# 3. 애플리케이션 실행
./gradlew bootRun
```

### Docker 실행

```bash
docker-compose up -d last-mile-driver-service
```

### Health Check

```bash
curl http://localhost:4021/actuator/health
```

---

## 프로젝트 구조

```
src/main/java/com/early_express/last_mile_driver_service/
├── domain/last_mile_driver/
│   ├── application/
│   │   ├── command/
│   │   │   ├── LastMileDriverCommandService.java
│   │   │   └── dto/
│   │   │       └── LastMileDriverCommandDto.java
│   │   │           ├── CreateCommand
│   │   │           ├── CreateResult
│   │   │           ├── AssignDeliveryCommand
│   │   │           ├── DriverAssignResult
│   │   │           ├── CompleteDeliveryCommand
│   │   │           └── CancelDeliveryCommand
│   │   └── query/
│   │       ├── LastMileDriverQueryService.java
│   │       └── dto/
│   │           └── LastMileDriverQueryDto.java
│   │               └── LastMileDriverResponse
│   │
│   ├── domain/
│   │   ├── exception/
│   │   │   ├── LastMileDriverErrorCode.java
│   │   │   └── LastMileDriverException.java
│   │   ├── model/
│   │   │   ├── LastMileDriver.java           # Aggregate Root
│   │   │   └── vo/
│   │   │       ├── LastMileDriverId.java
│   │   │       └── LastMileDriverStatus.java
│   │   └── repository/
│   │       └── LastMileDriverRepository.java
│   │
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── LastMileDriverEntity.java
│   │       ├── LastMileDriverJpaRepository.java
│   │       └── LastMileDriverRepositoryImpl.java
│   │
│   └── presentation/
│       ├── internal/
│       │   ├── LastMileDriverInternalController.java
│       │   └── dto/
│       │       ├── request/
│       │       │   ├── LastMileDriverCreateRequest.java
│       │       │   ├── DriverAssignRequest.java
│       │       │   ├── DriverCompleteRequest.java
│       │       │   └── DriverCancelRequest.java
│       │       └── response/
│       │           ├── LastMileDriverCreateResponse.java
│       │           ├── DriverAssignResponse.java
│       │           └── DriverOperationResponse.java
│       └── web/
│           ├── hubmanager/
│           │   ├── LastMileDriverHubManagerController.java
│           │   └── dto/response/
│           │       └── HubManagerLastMileDriverResponse.java
│           └── master/
│               ├── LastMileDriverMasterController.java
│               └── dto/response/
│                   └── MasterLastMileDriverResponse.java
│
└── global/
    ├── common/dto/
    │   └── PageInfo.java
    └── presentation/dto/
        ├── ApiResponse.java
        └── PageResponse.java
```

---

## 에러 코드

| 에러 코드 | HTTP Status | 설명 |
|----------|-------------|------|
| `DRIVER_NOT_FOUND` | 404 | 드라이버를 찾을 수 없음 |
| `DRIVER_NOT_AVAILABLE` | 400 | 드라이버가 배정 가능한 상태가 아님 |
| `DRIVER_NOT_ON_DELIVERY` | 400 | 드라이버가 배송 중이 아님 |
| `DRIVER_ALREADY_ON_DELIVERY` | 400 | 드라이버가 이미 배송 중 |
| `NO_AVAILABLE_DRIVER` | 400 | 배정 가능한 드라이버가 없음 |
| `INVALID_HUB_ID` | 400 | 유효하지 않은 허브 ID |

---

## 보안

- **OAuth 2.0 Resource Server**: Keycloak JWT 토큰 검증
- **역할별 접근 제어**:
    - Internal API: 서비스 간 통신만 허용 (Gateway 미노출)
    - Hub Manager API: `HUB_MANAGER` 역할 필요, 자신의 허브만 조회
    - Master API: `MASTER` 역할 필요, 전체 조회 가능

---

## 모니터링

- **Actuator**: `/actuator/health`, `/actuator/info`
- **주요 메트릭**:
    - 허브별 가용 드라이버 수
    - 평균 배송 시간
    - 일별 배송 완료 건수

---

## 관련 서비스

| 서비스 | 연동 방식 | 역할 |
|--------|----------|------|
| **User Service** | Feign | 드라이버 등록 시 생성 요청 |
| **LastMileDelivery Service** | Feign | 배송 생성/완료/취소 시 드라이버 상태 관리 |
| **Hub Service** | - | 허브 정보 참조 (hubId) |