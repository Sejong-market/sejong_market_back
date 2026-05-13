# 중고거래 프로젝트 개발 계획

## 1) 프로젝트 개요
- 목표: Spring Boot 기반 중고거래 백엔드 MVP 구현
- 기술 스택: Java 17, Spring Boot 3.5.14, Gradle, Spring Data JPA, MySQL, Lombok
- 현재 상태: `controller/service/repository/entity/dto` 기본 파일 구조 구성 완료

## 2) MVP 범위
- 사용자: 회원가입, 로그인, 마이페이지(내 상품 목록/상태 확인)
- 상품: 등록, 목록 조회, 상세 조회
- 거래 상태: `FOR_SALE -> RESERVED -> SOLD_OUT` 전환
- 댓글: 상품 상세 하단 댓글 등록/조회(간이 채팅 대체)

## 3) 백엔드 개발 단계
### Step 1. Entity 설계
- `User`: id, email, password, nickname
- `Product`: id, title, content, price, status, sellerId
- `Comment`: id, productId, writerId, content
- 공통 기준: JPA 어노테이션, 기본 생성자 보호, enum은 문자열 저장

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
- `application.yml`에 MySQL 연결 정보 설정
- 로컬 DB 생성 및 테이블 자동 생성 확인
- 샘플 데이터 입력 후 API 동작 점검

### Step 6. 테스트
- 서비스 단위 테스트 작성
- 컨트롤러 통합 테스트 작성
- 최소 테스트 범위: 회원가입, 상품 등록, 상태 전환, 댓글 등록

## 4) 협업 브랜치 전략 (Git Flow)
- 브랜치 전략으로 **Git Flow**를 사용한다.
- `master`: 최종 제출용
- `develop`: 통합 개발용
- `feature/*`: 기능 단위 개발 브랜치

권장 흐름:
1. `develop`에서 `feature/기능명` 생성
2. 기능 구현 + 테스트
3. PR 생성 후 리뷰
4. 리뷰 반영 후 `develop` 병합

## 5) 커밋/PR 규칙
- 커밋 Prefix:
  - `Create`: 새 파일 생성
  - `Delete`: 파일/코드 삭제
  - `Modify`: 단순 수정
  - `Feat`: 기능 구현
  - `Test`: 테스트 추가
  - `Bug`: 버그 제보
  - `Fix`: 버그 수정

- PR 템플릿 권장 항목:
  - 변경 목적
  - 핵심 변경 사항
  - 테스트 결과
  - 리뷰 포인트

## 6) 코딩 규칙
- 변수명은 **파스칼 케이스(PascalCase)** 를 사용한다.

## 7) 실행 방법
1. MySQL 실행 및 DB 생성
2. `src/main/resources/application.yml` 설정
3. 프로젝트 실행
   - Windows: `gradlew.bat bootRun`
4. API 테스트(Postman 또는 Swagger)

## 8) 다음 작업 우선순위
1. Entity 필드/관계 매핑 완성
2. Repository를 `JpaRepository` 기반으로 전환
3. Service 비즈니스 로직 구현
4. Controller 엔드포인트 구현
5. 테스트 코드 작성
