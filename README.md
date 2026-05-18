# 중고거래 프로젝트 개발 계획

## 1) 프로젝트 개요
- 목표: Spring Boot 기반 중고거래 백엔드 MVP 구현
- 기술 스택: Java 17, Spring Boot 3.5.14, Gradle, Spring Data JPA, MySQL, Lombok
- 현재 상태: `com.example.market` 패키지 기준 계층형 디렉토리 구조 구성 완료
- 협업 규칙: [CONTRIBUTING.md](./CONTRIBUTING.md) 참고

## 2) MVP 범위
- 사용자: 회원가입, 로그인, 마이페이지(내 상품 목록/상태 확인)
- 상품: 등록, 목록 조회, 상세 조회
- 거래 상태: `FOR_SALE -> RESERVED -> SOLD_OUT` 전환
- 댓글: 상품 상세 하단 댓글 등록/조회(간이 채팅 대체)

## 3) 백엔드 개발 단계

### DB 설계 (ERD)

- **스키마명**: `SejongMarketDB`
- **문자셋**: `utf8mb4`
- **엔진**: InnoDB
- **Workbench 적용 시**: PK 컬럼은 `NOT NULL`, `created_at`은 `DATETIME` + `DEFAULT CURRENT_TIMESTAMP` 사용

#### 테이블 요약

| 테이블 | PK | 주요 컬럼 | 비고 |
|--------|-----|-----------|------|
| `users` | `userid` | `email`, `password`, `nickname`, `created_at` | `email` UNIQUE |
| `products` | `productid` | `title`, `content`, `price`, `status`, `sellerid`, `image_url`, `created_at`, `updated_at` | `sellerid` → `users.userid` |
| `comments` | `commentid` | `writerid`, `productid`, `content`, `created_at` | `writerid` → `users.userid`, `productid` → `products.productid` |

#### 관계 (FK)

```
users (1) ──< products (N)     sellerid
users (1) ──< comments (N)     writerid
products (1) ──< comments (N)  productid
```

#### `users`

| 컬럼 | 타입 | 제약 |
|------|------|------|
| `userid` | INT | PK, NOT NULL, AUTO_INCREMENT |
| `email` | VARCHAR(45) | NOT NULL, UNIQUE |
| `password` | VARCHAR(255) | NOT NULL |
| `nickname` | VARCHAR(45) | NOT NULL |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

#### `products`

| 컬럼 | 타입 | 제약 |
|------|------|------|
| `productid` | INT | PK, NOT NULL, AUTO_INCREMENT |
| `title` | VARCHAR(100) | NOT NULL |
| `content` | TEXT | NULL |
| `price` | INT | NOT NULL |
| `status` | ENUM | `FOR_SALE`, `RESERVED`, `SOLD_OUT` (기본값 `FOR_SALE`) |
| `sellerid` | INT | NOT NULL, FK → `users.userid` |
| `image_url` | VARCHAR(225) | NULL |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| `updated_at` | DATETIME | NOT NULL, ON UPDATE CURRENT_TIMESTAMP |

#### `comments`

| 컬럼 | 타입 | 제약 |
|------|------|------|
| `commentid` | INT | PK, NOT NULL, AUTO_INCREMENT |
| `writerid` | INT | NOT NULL, FK → `users.userid` |
| `productid` | INT | NOT NULL, FK → `products.productid` |
| `content` | TEXT | NOT NULL |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

### Step 1. Entity 설계
- `User` ↔ `users`: `userId`, `email`, `password`, `nickname`, `createdAt`
- `Product` ↔ `products`: `productId`, `title`, `content`, `price`, `status`, `sellerId`, `imageUrl`, `createdAt`, `updatedAt`
- `Comment` ↔ `comments`: `commentId`, `writerId`, `productId`, `content`, `createdAt`
- `ProductStatus`: `FOR_SALE`, `RESERVED`, `SOLD_OUT` (DB ENUM과 동일)
- 공통 기준: JPA 어노테이션, 기본 생성자 보호, FK는 `@ManyToOne` 관계 매핑

### Step 2. Repository 구현
- 각 Entity별 `JpaRepository` 상속
- 기본 CRUD 우선 적용
- 필요 시 조회 메서드 추가 (`findBySellerId`, `findByProductId` 등)

### Step 3. Service 로직 구현
- 사용자: 회원가입/로그인 검증 로직
- 상품: 등록/조회/상태 변경 로직
- 댓글: 등록/목록 조회 로직
- 예외 처리: 잘못된 요청, 리소스 없음, 권한 없음

### Step 4. Controller API 구현
- `/api/users`: 회원가입, 로그인, 마이페이지
- `/api/products`: 등록, 목록, 상세, 상태 변경
- `/api/comments`: 댓글 등록, 목록 조회
- 요청/응답 DTO 분리 및 입력값 검증 적용

### Step 5. DB 연동 및 설정
- MySQL에 `SejongMarketDB` 스키마 생성 (Workbench Forward Engineering SQL 또는 JPA `ddl-auto`)
- `application.yml` JDBC URL을 `jdbc:mysql://localhost:3306/SejongMarketDB` 로 설정
- `application-secret.yml`에 DB 비밀번호 설정
- 테이블·FK·UNIQUE 제약이 ERD와 일치하는지 확인
- 샘플 데이터 입력 후 API 동작 점검

### Step 6. 테스트
- 서비스 단위 테스트 작성
- 컨트롤러 통합 테스트 작성
- 최소 테스트 범위: 회원가입, 상품 등록, 상태 전환, 댓글 등록

## 4) 프로젝트 구조

### 전체 구조
```
sejong-market-backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/market/
│   │   │   ├── controller/          # API 요청 처리 (웨이터)
│   │   │   ├── service/             # 비즈니스 로직 (주방장)
│   │   │   ├── repository/          # DB 접근 (창고 관리자)
│   │   │   ├── entity/              # DB 테이블 매핑 (식재료)
│   │   │   ├── dto/                 # 데이터 전달 객체 (배달 박스)
│   │   │   ├── global/              # 공통 설정 및 유틸
│   │   │   └── MarketApplication.java
│   │   └── resources/
│   │       ├── application.yml      # 공개 설정
│   │       └── application-secret.yml # 비밀 설정 (.gitignore)
│   └── test/                        # 테스트 코드
├── build.gradle                     # 빌드 설정
├── settings.gradle
├── .gitignore
├── README.md
└── CONTRIBUTING.md
```

### 상세 구조 (계층형)
```
src/main/java/com/example/market/
│
├── controller/                      # Controller 계층
│   ├── UserController.java          #    - 사용자 관련 API
│   ├── ProductController.java       #    - 상품 관련 API
│   └── CommentController.java       #    - 댓글 관련 API
│
├── service/                         # Service 계층
│   ├── UserService.java             #    - 회원가입, 로그인 로직
│   ├── ProductService.java          #    - 상품 CRUD, 상태 변경 로직
│   └── CommentService.java          #    - 댓글 CRUD 로직
│
├── repository/                      # Repository 계층
│   ├── UserRepository.java          #    - 사용자 DB 접근
│   ├── ProductRepository.java       #    - 상품 DB 접근
│   └── CommentRepository.java       #    - 댓글 DB 접근
│
├── entity/                          # Entity (DB 테이블)
│   ├── User.java                    #    - 사용자 테이블
│   ├── Product.java                 #    - 상품 테이블
│   ├── Comment.java                 #    - 댓글 테이블
│   └── ProductStatus.java           #    - 상품 상태 Enum
│
├── dto/                             # DTO (데이터 전달)
│   ├── user/
│   │   ├── UserRequestDto.java      #    - 회원가입/로그인 요청
│   │   └── UserResponseDto.java     #    - 사용자 정보 응답
│   ├── product/
│   │   ├── ProductRequestDto.java   #    - 상품 등록/수정 요청
│   │   └── ProductResponseDto.java  #    - 상품 정보 응답
│   └── comment/
│       ├── CommentRequestDto.java   #    - 댓글 작성 요청
│       └── CommentResponseDto.java  #    - 댓글 정보 응답
│
├── global/                          # 공통 모듈
│   ├── config/                      #    - 설정 클래스
│   │   └── WebConfig.java           #      (CORS 등)
│   ├── exception/                   #    - 예외 처리
│   │   └── GlobalExceptionHandler.java
│   └── common/                      #    - 공통 클래스
│       └── BaseTimeEntity.java      #      (createdAt, updatedAt)
│
└── MarketApplication.java           # 메인 실행 클래스
```

### 계층별 역할
| 계층 | 역할 | 비유 |
|------|------|------|
| Controller | HTTP 요청/응답 처리 | 웨이터 (주문 받고 서빙) |
| Service | 비즈니스 로직 수행 | 주방장 (요리 담당) |
| Repository | DB CRUD 작업 | 창고 관리자 (재료 관리) |
| Entity | DB 테이블 매핑 | 식재료 (데이터 그 자체) |
| DTO | 계층 간 데이터 전달 | 배달 박스 (포장해서 전달) |
| Global | 공통 기능 모음 | 주방 도구 (모두가 사용) |

### 데이터 흐름
```
[클라이언트] → [Controller] → [Service] → [Repository] → [DB]
     ↑             ↓              ↓             ↓           ↓
   응답          DTO변환       로직처리      Entity사용    저장/조회
```

## 5) 실행 방법
1. MySQL 실행 후 `SejongMarketDB` 스키마 생성
   ```sql
   CREATE SCHEMA IF NOT EXISTS `SejongMarketDB` DEFAULT CHARACTER SET utf8mb4;
   ```
   - Workbench에서 ERD SQL을 실행하거나, 앱 실행 시 JPA가 테이블을 생성하도록 할 수 있음
2. `src/main/resources/application.yml`에서 DB URL이 `SejongMarketDB`를 가리키는지 확인
3. `src/main/resources/application-secret.yml`에 DB 비밀번호 설정
4. 프로젝트 실행
   - Windows: `gradlew.bat bootRun`
5. API 테스트(Postman 또는 Swagger)

## 6) 다음 작업 우선순위
1. ERD 기준 Entity 필드·FK 관계 매핑 (`imageUrl`, `createdAt`/`updatedAt` 포함)
2. Service 비즈니스 로직 구현
3. Controller 엔드포인트 구현
4. 테스트 코드 작성
