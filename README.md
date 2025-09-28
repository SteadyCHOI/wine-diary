# 🍷 Wine Diary - 와인 테이스팅 노트 앱

나만의 와인 테이스팅 경험을 기록하고 관리할 수 있는 웹 애플리케이션입니다.

## ✨ 주요 기능

### 🔐 인증 시스템
- **소셜 로그인**: 카카오, 구글 등을 통한 간편 로그인
- **JWT 토큰**: 안전한 사용자 인증

### 📝 테이스팅 노트 작성
- **와인 검색**: 실시간 와인 데이터베이스 검색
- **상세 평가**: 별점, 바디감, 탄닌, 산도, 단맛 등 체계적 평가
- **개인 노트**: 자유로운 텍스트 메모 작성

### 📚 노트 관리
- **목록 조회**: 작성한 모든 테이스팅 노트 확인
- **검색 & 필터링**: 와인명, 평점, 기간별 필터링
- **정렬**: 최신순, 평점순, 와인명순 정렬
- **상세보기**: 모달 팝업으로 노트 상세 내용 확인
- **수정 & 삭제**: 작성한 노트 편집 및 삭제

### 📊 통계 & 분석
- **개인 통계**: 총 노트 수, 평균 평점, 이번 달 활동량
- **취향 분석**: 와인 타입별 선호도 분석
- **월별 통계**: 테이스팅 활동 추이

## 🏗️ 기술 스택

### Frontend
- **HTML5** + **CSS3** + **JavaScript (ES6+)**
- **Font Awesome** - 아이콘
- **반응형 디자인** - 모바일 친화적

### Backend
- **Java 17** + **Spring Boot 3.2.0**
- **Spring Data JPA** - 데이터 액세스
- **Spring Security** - 보안
- **JWT** - 인증 토큰
- **MySQL 8.0** - 데이터베이스

### 개발 도구
- **Maven** - 빌드 도구
- **Docker** - 컨테이너화
- **Lombok** - 코드 간소화

## 🚀 실행 방법

### 1. 백엔드 서버 시작

#### Docker를 사용하는 경우 (권장)
```bash
cd backend
docker-compose up -d
```

#### 로컬에서 직접 실행하는 경우
```bash
# MySQL 서버 실행 후
cd backend
mvn spring-boot:run
```

### 2. 프론트엔드 실행

웹 서버(Apache, Nginx) 또는 간단한 HTTP 서버로 실행:

```bash
# Python 3 사용시
python -m http.server 3000

# Node.js 사용시
npx http-server -p 3000

# Live Server (VS Code 확장) 사용 권장
```

### 3. 브라우저에서 접속

```
http://localhost:3000
```

### 4. 페이지 플로우

```
index.html (로그인)
    ↓ 로그인 성공
dashboard.html (메인 허브)
    ↓ 네비게이션 메뉴
├── write-simple.html (노트 작성)
└── notes.html (노트 목록 및 관리)
```

## 📁 프로젝트 구조

```
wineDiary/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/winediary/
│   │   ├── controller/         # REST API 컨트롤러
│   │   ├── service/           # 비즈니스 로직
│   │   ├── repository/        # 데이터 액세스
│   │   ├── entity/            # JPA 엔티티
│   │   ├── dto/              # 데이터 전송 객체
│   │   └── config/           # 설정
│   ├── src/main/resources/
│   │   ├── application.yml    # 앱 설정
│   │   └── data.sql          # 샘플 데이터
│   └── docker-compose.yml     # Docker 설정
│
├── frontend/                   # 웹 프론트엔드
│   ├── index.html             # 메인 페이지
│   ├── dashboard.html         # 대시보드
│   ├── write.html             # 노트 작성
│   ├── notes.html             # 노트 목록
│   ├── styles.css             # 스타일시트
│   └── *.js                   # JavaScript 파일
│
└── README.md                  # 프로젝트 문서
```

## 🌟 주요 페이지

### 1. 대시보드 (`dashboard.html`)
- 메인 네비게이션 허브
- 최근 활동 확인
- 빠른 액세스 메뉴

### 2. 노트 작성 (`write.html`)
- 와인 검색 및 선택
- 체계적인 평가 입력
- 개인 노트 작성

### 3. 노트 목록 (`notes.html`)
- 작성한 노트 전체 조회
- 고급 검색 및 필터링
- 통계 정보 표시

## 🔧 설정

### 백엔드 설정
`backend/src/main/resources/application.yml` 파일에서:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wine_diary
    username: your_username
    password: your_password

jwt:
  secret: your-jwt-secret-key
```

### 카카오 로그인 설정
JavaScript 파일에서 카카오 앱 키 설정:

```javascript
Kakao.init('YOUR_KAKAO_APP_KEY');
```

## 📊 API 엔드포인트

### 인증
- `POST /api/v1/auth/login` - 로그인
- `GET /api/v1/auth/validate` - 토큰 검증

### 와인
- `GET /api/v1/wines/search?keyword={keyword}` - 와인 검색
- `POST /api/v1/wines` - 와인 등록

### 테이스팅 노트
- `GET /api/v1/tasting-notes/my` - 내 노트 목록
- `POST /api/v1/tasting-notes` - 노트 생성
- `GET /api/v1/tasting-notes/{id}` - 노트 상세
- `PUT /api/v1/tasting-notes/{id}` - 노트 수정
- `DELETE /api/v1/tasting-notes/{id}` - 노트 삭제

### 사용자
- `GET /api/v1/users/profile` - 프로필 조회

## 🎯 향후 계획

- [ ] 와인 추천 시스템
- [ ] 소셜 기능 (친구, 공유)
- [ ] 고급 통계 차트
- [ ] 이미지 업로드
- [ ] PWA (Progressive Web App)
- [ ] 다국어 지원

## 🤝 기여하기

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 라이센스

This project is licensed under the MIT License.

## 📞 문의

프로젝트에 대한 문의사항이나 버그 리포트는 이슈로 등록해 주세요.

---

**🍷 Cheers to Great Wine Experiences! 🍷**