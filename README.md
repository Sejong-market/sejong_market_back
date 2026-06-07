# 🛒 세종 마켓 (Sejong Market) - 백엔드

세종대학교 구성원을 위한 중고거래 플랫폼, **세종 마켓**의 백엔드 시스템입니다.  
본 프로젝트는 **'오픈소스SW개론'** 최종 프로젝트로 개발되었으며, Git Flow 기법을 활용하여 체계적으로 협업을 진행하였습니다.

---

## 1. 프로젝트 개요
- **프로젝트 명**: 세종 마켓 (Sejong Market)
- **목표**: 교내 구성원들이 안전하고 편리하게 중고 물품을 거래할 수 있는 REST API 서버 구축
- **주요 기능**:
  - **사용자**: JWT 기반 회원가입/로그인, 마이페이지, 회원 탈퇴(데이터 연쇄 삭제)
  - **상품**: 이미지 업로드를 포함한 상품 등록, 전체 목록 및 상세 조회, 판매 상태 관리
  - **댓글**: 상품별 실시간 소통을 위한 댓글 기능

---

## 2. 협업 과정 및 브랜치 전략 (Git Flow)

본 팀은 프로젝트의 안정성과 체계적인 코드 관리를 위해 **Git Flow** 전략을 채택하였습니다.

### 🌿 브랜치 구성
- **main**: 제품으로 출시될 수 있는 상태의 코드를 관리 (v1.0.0 태깅)
- **develop**: 다음 출시 버전을 대비해 개발 중인 코드를 통합
- **feature/**: 새로운 기능 구현 (예: `feature/user-auth`, `feature/product-api`)
- **release/**: 출시 준비를 위한 최종 버그 수정 및 문서 작업 (`release/v1.0.0`)
- **hotfix/**: 출시 버전에서 발생한 긴급 버그 수정

### 🤝 역할 분담 및 협업 규칙
- **팀장**: 초기 환경 세팅, Pull Request(PR) 검토 및 승인, 전체적인 브랜치 병합 관리
- **팀원**: 할당된 이슈(Issue)에 따라 기능별 `feature` 브랜치 생성 및 구현 후 `develop`으로 PR 요청
- **규칙**: 
  - `develop` 혹은 `main` 브랜치에 직접 Push 금지
  - 커밋 메시지는 `feat:`, `fix:`, `docs:` 등의 접두어를 사용하는 Conventional Commits 준수
  - 코드 리뷰 후 최소 1명 이상의 승인을 얻어야 병합 진행

---

## 3. 기술 스택 (Tech Stack)
- **Language**: Java 17
- **Framework**: Spring Boot 3.5.14
- **Security**: Spring Security, JWT (JSON Web Token)
- **Data**: Spring Data JPA, MySQL 8.0
- **Build**: Gradle
- **Tools**: Git, GitHub, Postman, MySQL Workbench

---

## 4. 시작하기 (Installation & Setup)

### 데이터베이스 설정
MySQL에서 아래 스키마를 생성합니다.
```sql
CREATE SCHEMA IF NOT EXISTS `SejongMarketDB` DEFAULT CHARACTER SET utf8mb4;
```

### 환경 변수 설정
`src/main/resources/application-secret.yml` 파일을 생성하여 DB 비밀번호를 설정합니다.
```yaml
spring:
  datasource:
    password: YOUR_PASSWORD
```

### 서버 실행
```bash
./gradlew bootRun
```

---

## 5. API 명세서 (API Specification)

### 👤 사용자 (User)
| 기능 | Method | Endpoint | 설명 |
|------|--------|----------|------|
| 회원가입 | POST | `/api/users/signUp` | 이메일, 비밀번호, 닉네임 등록 |
| 로그인 | POST | `/api/users/login` | JWT 토큰 발급 (응답 헤더 확인) |
| 내 정보 조회 | GET | `/api/users/mypage` | 로그인한 사용자의 프로필 조회 |
| 내 정보 수정 | PATCH | `/api/users/mypage` | 닉네임 또는 비밀번호 수정 |
| 내 상품 조회 | GET | `/api/users/mypage/products` | 내가 등록한 상품 목록 조회 |
| 회원 탈퇴 | DELETE | `/api/users/mypage` | 계정 및 관련 데이터(상품, 댓글) 삭제 |

### 📦 상품 (Product)
| 기능 | Method | Endpoint | 설명 |
|------|--------|----------|------|
| 상품 등록 | POST | `/api/products` | 이미지 및 상품 정보 등록 (Multipart 요청) |
| 상품 목록 | GET | `/api/products` | 판매 중인 상품 최신순 조회 (페이징 지원) |
| 상세 조회 | GET | `/api/products/{id}` | 상품 상세 및 댓글 목록 조회 |
| 상태 변경 | PATCH | `/api/products/{id}/status` | 상품 상태(판매중/예약중/완료) 변경 |

### 💬 댓글 (Comment)
| 기능 | Method | Endpoint | 설명 |
|------|--------|----------|------|
| 댓글 작성 | POST | `/api/comments` | 특정 상품에 댓글 작성 |

---
**© 2026 Sejong Market Project Team**  
This project is licensed under the [MIT License](LICENSE).
