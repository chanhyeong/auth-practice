# JWT 인증 웹 애플리케이션 PRD (Product Requirements Document)

## 1. 프로젝트 개요

### 1.1 목표
JWT 기반 인증 시스템을 제공하는 웹 애플리케이션 구현

### 1.2 범위
- 사용자 인증 및 권한 관리
- 토큰 기반 세션 관리
- OAuth 2.0 연동 기반 확장 가능한 아키텍처

## 2. 기술 스택

### 2.1 Backend
- **언어**: Kotlin
- **프레임워크**: Spring Boot 3.x
- **보안**: Spring Security 6.x
- **데이터베이스**: H2 Database (개발/테스트용)
- **ORM**: Spring Data JDBC
- **토큰**: JWT (jsonwebtoken 라이브러리)
- **서버 포트**: 8080

### 2.2 Frontend
- **권장**: React/Next.js 또는 Vue.js/Nuxt.js
- **대안**: Angular, Svelte 등 (자유 선택)
- **개발 서버 포트**: 3000

### 2.3 개발 환경 설정
- **Frontend**: http://localhost:3000
- **Backend**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:authdb;MODE=MySQL;DB_CLOSE_DELAY=-1`
    - Username: `sa`
    - Password: (빈 값)
- **CORS 설정**: Frontend에서 Backend로의 요청 허용
- **API Base URL**: http://localhost:8080/api

### 2.4 기타
- **API 문서화**: Swagger/OpenAPI 3.0
- **빌드 도구**: Gradle (Kotlin DSL)
- **컨테이너**: Docker (옵션)

## 3. 기능 요구사항

### 3.1 인증 기능

#### 3.1.1 로그인/로그아웃
- **이메일 + 비밀번호** 기반 1차 인증
- **OTP 기반 2차 인증** (필수)
- 구글 로그인과 동일한 **UI/UX 흐름**
- 로그아웃 시 토큰 무효화
- "로그인 상태 유지" 옵션

#### 3.1.2 OTP (One-Time Password) 2차 인증
- **인증 흐름**: ID/PW 입력 → OTP 입력 → 로그인 완료
- **OTP 표시**: 개발 편의를 위해 같은 페이지에 OTP 번호 표시
- **유효시간**: 60초 (만료 시 자동 갱신)
- **보안 정책**:
    - 만료된 OTP로는 인증 불가
    - 사용된 OTP는 즉시 무효화
    - 연속 실패 시 일시적 계정 잠금
- **OTP 갱신**: 만료 또는 사용자 요청 시 새로운 OTP 생성

#### 3.1.3 회원가입
- 이메일 인증 기반 회원가입
- 비밀번호 강도 검증
- 이용약관 및 개인정보처리방침 동의
- 중복 이메일 검증

#### 3.1.4 비밀번호 관리
- 비밀번호 재설정 (이메일 링크)
- 비밀번호 변경
- 비밀번호 정책 적용 (최소 8자, 대소문자/숫자/특수문자 조합)

### 3.2 토큰 관리

#### 3.2.1 JWT 토큰 정책
- **Access Token**: 유효기간 24시간
- **Refresh Token**: 유효기간 30일
- 토큰 자동 갱신 메커니즘
- 토큰 블랙리스트 관리

#### 3.2.2 보안 강화
- 토큰 서명 알고리즘: RS256
- 토큰 탈취 방지를 위한 HttpOnly 쿠키 옵션
- CSRF 보호
- Rate limiting 적용

### 3.3 사용자 관리

#### 3.3.1 프로필 관리
- 개인정보 조회/수정
- 프로필 이미지 업로드
- 계정 비활성화/탈퇴

#### 3.3.2 권한 관리
- 역할 기반 접근 제어 (RBAC)
- 기본 역할: USER, ADMIN
- 권한별 API 접근 제한

### 3.4 OAuth 2.0 연동 (향후 확장)

#### 3.4.1 지원 예정 제공자
- Google OAuth 2.0
- GitHub OAuth
- Kakao/Naver (국내 서비스 고려시)

#### 3.4.2 연동 기능
- 소셜 로그인
- 계정 연결/해제
- 여러 제공자 동시 연결 지원

## 4. 비기능 요구사항

### 4.1 보안
- HTTPS 필수
- SQL Injection 방지
- XSS 방지
- CORS 정책 적용
- 로그인 시도 제한 (5회 실패시 15분 잠금)

### 4.2 성능
- API 응답시간 500ms 이하
- 동시 사용자 1,000명 지원
- 토큰 검증 캐싱

### 4.3 가용성
- 로컬 개발 환경에 최적화된 구성
- H2 인메모리 모드로 앱 재시작 시 데이터 초기화
- 빠른 개발 사이클 지원

### 4.4 사용성
- 모바일 반응형 디자인
- 웹 접근성 (WCAG 2.1 AA 수준)
- 다국어 지원 (한국어, 영어)

## 5. UI/UX 요구사항

### 5.1 화면 구성 (구글 로그인 참조)
1. **로그인 페이지 (1차 인증)**
    - 이메일 입력 → 다음 버튼
    - 비밀번호 입력 → 다음 버튼 (OTP 화면으로 이동)
    - "계정 만들기" 링크
    - "비밀번호를 잊으셨나요?" 링크

2. **OTP 인증 페이지 (2차 인증)**
    - OTP 번호 표시 영역 (개발용 - 실제로는 표시하지 않음)
    - OTP 입력 필드 (6자리)
    - 남은 시간 표시 (60초 카운트다운)
    - "새 OTP 받기" 버튼
    - "이전 단계" 버튼
    - 로그인 완료 버튼

3. **회원가입 페이지**
    - 이름, 이메일, 비밀번호 입력
    - 약관 동의 체크박스
    - 이메일 인증 단계

4. **대시보드**
    - 사용자 프로필 정보
    - 토큰 상태 표시
    - 로그아웃 버튼

### 5.2 디자인 가이드라인
- Material Design 또는 유사한 모던 디자인 시스템
- 일관된 색상 팔레트 및 타이포그래피
- 로딩 상태 및 에러 메시지 표시

## 6. API 설계

### 6.1 인증 API
```
POST /api/auth/login           # 1차 인증 (ID/PW)
POST /api/auth/verify-otp      # 2차 인증 (OTP 검증)
POST /api/auth/generate-otp    # 새 OTP 생성
POST /api/auth/logout          # 로그아웃
POST /api/auth/register        # 회원가입
POST /api/auth/refresh         # 토큰 갱신
POST /api/auth/forgot-password # 비밀번호 재설정 요청
POST /api/auth/reset-password  # 비밀번호 재설정
```

### 6.2 사용자 API
```
GET  /api/users/me             # 내 정보 조회
PUT  /api/users/me             # 내 정보 수정
POST /api/users/change-password # 비밀번호 변경
DELETE /api/users/me           # 계정 탈퇴
```

### 6.3 관리자 API
```
GET  /api/admin/users          # 사용자 목록 조회
PUT  /api/admin/users/{id}     # 사용자 정보 수정
DELETE /api/admin/users/{id}   # 사용자 삭제
```

## 7. 데이터베이스 설계

### 7.1 주요 테이블 (H2 MySQL 호환 모드 + Spring Data JDBC)
- **users**: 사용자 기본 정보
  ```sql
  CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    profile_image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  );
  ```

- **user_roles**: 사용자 권한
  ```sql
  CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  );
  ```

- **otp_codes**: OTP 코드 관리
  ```sql
  CREATE TABLE otp_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  );
  ```

- **refresh_tokens**: 리프레시 토큰 관리
  ```sql
  CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
  );
  ```

- **token_blacklist**: 무효화된 토큰
  ```sql
  CREATE TABLE token_blacklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_jti VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  );
  ```

- **oauth_providers**: OAuth 연동 정보 (향후)
  ```sql
  CREATE TABLE oauth_providers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_provider_user (provider, provider_user_id)
  );
  ```

### 7.2 H2 Database 설정
- **로컬 환경**: 인메모리 모드 (`jdbc:h2:mem:authdb;MODE=MySQL;DB_CLOSE_DELAY=-1`)
- **테스트 환경**: 인메모리 모드 (`jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1`)
- **MySQL 호환 모드**: H2의 MySQL 호환성 모드 활성화
- **H2 Console**: 로컬 개발 시 웹 콘솔 활성화 (`/h2-console`)
- **데이터 초기화**: 앱 시작 시마다 스키마 및 데이터 자동 초기화
- **스키마 생성**: `schema.sql` 파일로 테이블 자동 생성
- **초기 데이터**: `data.sql` 파일로 기본 데이터 로딩

### 7.3 Spring Data JDBC 특성
- 단순한 POJO 매핑 (JPA 어노테이션 없음)
- 1:1, 1:N 관계는 Aggregate 패턴으로 처리
- 복잡한 조인 쿼리는 `@Query` 어노테이션 활용
- 트랜잭션 관리는 `@Transactional` 사용
- H2 MySQL 호환 모드로 표준 SQL 사용 가능
- 앱 시작 시 `schema.sql`과 `data.sql` 자동 실행

### 7.4 인덱스 전략
```sql
-- 이메일 조회 최적화
CREATE INDEX idx_users_email ON users(email);

-- OTP 조회 최적화
CREATE INDEX idx_otp_codes_user_id ON otp_codes(user_id);
CREATE INDEX idx_otp_codes_expires_at ON otp_codes(expires_at);
CREATE INDEX idx_otp_codes_code_user ON otp_codes(otp_code, user_id);

-- 토큰 조회 최적화  
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- 블랙리스트 토큰 조회 최적화
CREATE INDEX idx_token_blacklist_jti ON token_blacklist(token_jti);

-- 만료된 토큰 정리 최적화
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_token_blacklist_expires_at ON token_blacklist(expires_at);
```

## 8. 보안 고려사항

### 8.1 토큰 보안
- 짧은 Access Token 수명
- Refresh Token rotation
- 토큰 저장소 보안 (Redis 권장)

### 8.2 데이터 보안
- 비밀번호 bcrypt 해시
- 개인정보 암호화 저장
- 민감한 로그 마스킹

### 8.3 OTP 보안
- **OTP 생성**: 암호학적으로 안전한 6자리 숫자
- **유효시간**: 60초 (만료 시 자동 무효화)
- **일회성**: 사용 즉시 무효화 처리
- **브루트 포스 방지**: 연속 실패 시 계정 임시 잠금
- **만료된 OTP 정리**: 배치 작업으로 만료된 OTP 삭제

## 9. 테스트 요구사항

### 9.1 단위 테스트
- 서비스 로직 테스트 커버리지 90% 이상
- JWT 토큰 생성/검증 테스트
- OTP 생성/검증/만료 로직 테스트

### 9.2 통합 테스트
- API 엔드포인트 테스트
- 데이터베이스 연동 테스트
- OTP 인증 플로우 테스트

### 9.3 E2E 테스트
- 전체 로그인 시나리오 (ID/PW → OTP → 로그인 완료)
- 회원가입 전체 프로세스
- OTP 만료/갱신 시나리오

## 10. 배포 및 운영

### 10.1 환경 구성
- **로컬(local)**: H2 인메모리 모드 (MySQL 호환), H2 Console 활성화
- **테스트(test)**: H2 인메모리 모드 (MySQL 호환), 테스트 전용 스키마
- 환경별 설정 분리 (`application-local.yml`, `application-test.yml`)

### 10.2 데이터 관리
- **스키마 초기화**: `schema.sql`을 통한 자동 테이블 생성
- **초기 데이터**: `data.sql`을 통한 기본 데이터 로딩
- **데이터 영속성**: 인메모리 모드로 앱 재시작 시 초기화
- **테스트 격리**: 각 테스트마다 독립적인 H2 인스턴스

### 10.4 모니터링 (간소화)
- 애플리케이션 로그 출력
- 기본적인 Spring Boot Actuator 활용
- H2 Console을 통한 데이터 모니터링

### 10.3 CI/CD (간소화)
- **로컬 개발**: IDE 내장 테스트 실행
- **자동 테스트**: 코드 변경 시 자동 테스트 실행
- **간단한 빌드**: Gradle 기반 로컬 빌드

## 11. 마일스톤

### Phase 1 (2주)
- 기본 인증 시스템 구현 (ID/PW + OTP 2차 인증)
- JWT 토큰 관리
- OTP 생성/검증/만료 시스템
- 기본 UI 구현 (로그인/OTP 화면)

### Phase 2 (1주)
- 사용자 관리 기능
- 권한 관리 시스템
- API 문서화

### Phase 3 (1주)
- OAuth 2.0 기반 확장
- 보안 강화
- 성능 최적화

### Phase 4 (1주)
- 테스트 완성
- 배포 환경 구성
- 문서화 완료

## 12. 리스크 및 대응방안

### 12.1 기술적 리스크
- **토큰 보안 취약점**: 정기적인 보안 검토 및 업데이트
- **성능 병목**: 캐싱 전략 및 DB 최적화
- **확장성 문제**: 마이크로서비스 아키텍처 고려

### 12.2 비즈니스 리스크
- **사용자 경험**: 지속적인 UX 테스트 및 개선
- **규정 준수**: GDPR, 개인정보보호법 준수

## 13. 참고사항

### 13.1 JWT 자동 갱신 관련
JWT 스펙 자체에는 자동 갱신 메커니즘이 없으므로, Refresh Token을 활용한 토큰 갱신 전략을 사용합니다:
- Short-lived Access Token (24시간)
- Long-lived Refresh Token (30일)
- Silent refresh 메커니즘으로 사용자 경험 개선

### 13.2 OTP 2차 인증 관련
- **개발 편의성**: OTP 번호를 화면에 표시하여 테스트 용이
- **보안 강화**: 실제 운영에서는 SMS/이메일로 전송하거나 TOTP 앱 연동 고려
- **만료 정책**: 60초 유효시간으로 보안과 사용성의 균형 유지
- **확장 가능성**: Google Authenticator, SMS 인증 등으로 확장 가능

### 13.3 H2 Database 및 Spring Data JDBC 관련
- **H2 장점**: 빠른 개발/테스트, 별도 DB 서버 불필요, 가벼운 설정
- **H2 특성**:
    - 인메모리 모드로 앱 재시작 시마다 깨끗한 상태
    - MySQL 호환 모드로 구동하여 SQL 호환성 확보
    - 개발/테스트에 최적화된 환경
    - 일관된 개발 환경 보장
- **Spring Data JDBC 특징**: JPA 대비 간단한 매핑, 명시적 SQL 제어 가능
- **개발 효율성**: 설정 단순화, 빠른 피드백 사이클

### 13.4 확장 고려사항
- OAuth 2.0 Provider로서의 역할 (다른 서비스에 인증 제공)
- 멀티테넌트 지원
- API Rate limiting 고도화
- TOTP (Time-based OTP) 앱 연동 (Google Authenticator, Authy 등)
- SMS/이메일 기반 OTP 전송
- 실제 서비스 적용 시 MySQL/PostgreSQL 마이그레이션 고려