<!-- prettier-ignore-start -->

## ⌨️ BE Developer

|                                                            BE Developer 1                                                            |                                                            BE Developer 2                                                            |
| :----------------------------------------------------------------------------------------------------------------------------------: | :----------------------------------------------------------------------------------------------------------------------------------: |
| <a href="https://github.com/HeejuKo"><img src="https://avatars.githubusercontent.com/u/142784710?v=4" width="120px;" alt=""/></a>    | <a href="https://github.com/HeewonJo"><img src="https://avatars.githubusercontent.com/u/155250845?v=4" width="120px;" alt=""/></a>    |
|                                                                 고희주                                                                 |                                                                 조희원                                                                 |

---

## 🛠 기술 스택

| **역할**     | **종류**                                                                                                                                                                                                                 | **선정 이유**                     |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------- |
| Framework    | ![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)                                                                                                       | 생산성 높은 Java 기반 웹 프레임워크 |
| Language     | ![Java17](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=openjdk&logoColor=white)                                                                                                                | 안정적이고 대규모 프로젝트에 적합   |
| ORM          | ![JPA](https://img.shields.io/badge/JPA%20(Hibernate)-59666C?style=for-the-badge&logo=hibernate&logoColor=white)                                                                                                        | 객체지향적인 DB 접근                |
| DB           | ![MySQL](https://img.shields.io/badge/MySQL%208-4479A1?style=for-the-badge&logo=mysql&logoColor=white)                                                                                                                   | 안정적이고 널리 사용되는 RDBMS      |
| Cache/Queue  | ![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)                                                                                                                       | 캐시 및 세션 저장, 알림 큐 처리      |
| Auth         | ![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)                                                                                                                   | 토큰 기반 인증                      |
| Notification | ![WebPush](https://img.shields.io/badge/WebPush-5A0FC8?style=for-the-badge&logo=GoogleChrome&logoColor=white)                                                                                                            | 브라우저 푸시 알림 지원              |
| Deployment   | ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)        | 컨테이너 기반 배포, 클라우드 확장성  |
| CI/CD        | ![GitHubActions](https://img.shields.io/badge/GitHubActions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)                                                                                               | 자동화된 빌드/배포 파이프라인        |

---

## ✅ 실행 방법

### 1. 로컬 개발
<pre>
# 의존성 설치
./gradlew build

# 로컬 실행
./gradlew bootRun
</pre>

### 2. 도커 실행
<pre>
docker compose up -d mysql redis
docker compose up -d app
</pre>

## 📂 프로젝트 구조
```
📦2025-LIKELION-BuyDobong-BE
 ┣ 📂src/main/java/com/dobongsoon/BuyDobong
 ┃ ┣ 📂common         # 공통 설정/예외/응답
 ┃ ┗ 📂domain
 ┃   ┣ 📂auth    	  # 회원가입/로그인
 ┃   ┣ 📂favorite     # 관심 상점
 ┃   ┣ 📂keyword      # 관심 키워드
 ┃   ┣ 📂notification # 알림
 ┃   ┣ 📂product      # 상품
 ┃   ┣ 📂push         # PWA 웹 푸시
 ┃   ┣ 📂recent       # 최근 본 상점
 ┃   ┣ 📂search       # 상점/상품 검색
 ┃   ┣ 📂sms          # sms 인증
 ┃   ┣ 📂store        # 상점
 ┃   ┗ 📂user         # 사용자
 ┣ 📂src/main/resources
 ┃ ┗ 📜application-prod.yml
 ┣ 📜build.gradle
 ┗ 📜Dockerfile
```

 ## 📄 API Docs
	•	Swagger UI : https://n0t4u.shop/swagger-ui/index.html
