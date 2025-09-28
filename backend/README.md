# Wine Diary Backend

와인 테이스팅 노트를 기록하고 관리할 수 있는 백엔드 API 서버입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.0**
- **JWT**
- **Maven**
- **Lombok**

## 주요 기능

### 인증
- JWT 기반 인증
- 소셜 로그인 지원 (Kakao, Google 등)

### 와인 관리
- 와인 등록 및 검색
- 와인 타입별 조회
- 인기 와인 / 고평점 와인 조회

### 테이스팅 노트
- 테이스팅 노트 CRUD
- 평점 및 특성 기록 (바디감, 탄닌, 산도, 단맛)
- 페이징 처리된 노트 조회
- 월별 통계
- 와인 타입 선호도 분석

### 사용자 관리
- 사용자 프로필 조회
- 테이스팅 통계 제공

## API 엔드포인트

### 인증 (`/api/v1/auth`)
- `POST /login` - 로그인
- `GET /validate` - 토큰 유효성 검사

### 사용자 (`/api/v1/users`)
- `GET /profile` - 사용자 프로필 조회

### 와인 (`/api/v1/wines`)
- `POST /` - 와인 등록
- `GET /search?keyword={keyword}` - 와인 검색
- `GET /type/{type}` - 타입별 와인 조회
- `GET /popular` - 인기 와인 조회
- `GET /high-rated` - 고평점 와인 조회

### 테이스팅 노트 (`/api/v1/tasting-notes`)
- `POST /` - 테이스팅 노트 생성
- `GET /my` - 내 테이스팅 노트 조회
- `GET /my/page` - 페이징된 내 테이스팅 노트 조회
- `GET /{noteId}` - 테이스팅 노트 상세 조회
- `PUT /{noteId}` - 테이스팅 노트 수정
- `DELETE /{noteId}` - 테이스팅 노트 삭제
- `GET /statistics/monthly` - 월별 통계
- `GET /preferences/wine-type` - 와인 타입 선호도

## 설정 및 실행

### 1. 데이터베이스 설정
MySQL에서 데이터베이스 생성:
```sql
CREATE DATABASE wine_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 환경 변수 설정
`application.yml` 파일에서 다음 설정 수정:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wine_diary
    username: your_mysql_username
    password: your_mysql_password

jwt:
  secret: your-jwt-secret-key
```

### 3. 애플리케이션 실행
```bash
mvn spring-boot:run
```

서버는 `http://localhost:8080`에서 실행됩니다.

## 데이터베이스 스키마

### Users (사용자)
- `user_id` (PK)
- `name`, `email`, `provider`, `provider_id`
- `created_at`, `updated_at`

### Wines (와인)
- `wine_id` (PK)
- `name`, `type`, `region`, `producer`
- `vintage_year`, `alcohol_content`, `grape_varieties`

### Tasting Notes (테이스팅 노트)
- `note_id` (PK)
- `user_id` (FK), `wine_id` (FK)
- `rating`, `body_level`, `tannin_level`, `acidity_level`, `sweetness_level`
- `notes`, `tasting_date`

## 주요 JPQL 쿼리

- 사용자별 테이스팅 노트 조회
- 와인 검색 (이름, 생산자, 지역)
- 월별 테이스팅 통계
- 와인 타입별 선호도 분석
- 인기 와인 / 고평점 와인 조회

## 보안

- Spring Security를 통한 CORS 설정
- JWT 토큰 기반 인증
- 입력값 검증 (`@Valid`)
- 전역 예외 처리