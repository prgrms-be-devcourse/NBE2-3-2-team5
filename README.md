# 🎊 축제 정보 공유 및 동행 서비스앱 페스티모

<br>

## 프로젝트 소개

- 날짜, 지역 등으로 정리된 축제 정보를 제공해 사용자가 필요한 정보를 쉽게 찾을 수 있는 환경을 제공
- 축제 동행자를 모집하고 소통할 수 있는 커뮤니티 기능 제공
- 축제 경험을 공유하고 싶은 사람들,특히 자신이 다녀온 축제에 대한 리뷰를 통해
  다른 사람들에게 도움을 주고 싶은 사람들을 위한 공간
- 축제에 대한 정보를 공유하고, 동행자를 만나고, 리뷰를 남기는 등의 과정을 통해 축제를 즐기는 새로운 문화를 창출

<br>

## 팀원 구성

<div align="center">

|                                                                   **김태영**                                                                   |                                                                  **남주연**                                                                  |                                                                 **박현욱**                                                                 |                                                               **이하연**                                                               |                                                               **황규리**                                                               |
|:-------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------------------------------:|
| [<img src="https://avatars.githubusercontent.com/u/151701820?v=4" height=125 width=150> <br/> @taeyoung789](https://github.com/taeyoung789) | [<img src="https://avatars.githubusercontent.com/u/97582404?v=4" height=125 width=150> <br/> @juyeon6069](https://github.com/juyeon6069) | [<img src="https://avatars.githubusercontent.com/u/161629276?v=4" height=125 width=150> <br/> @Wookindeye](https://github.com/Wookindeye) | [<img src="https://avatars.githubusercontent.com/u/170444258?v=4" height=125 width=150> <br/> @exxyeon](https://github.com/exxyeon) | [<img src="https://avatars.githubusercontent.com/u/117248176?v=4" height=125 width=150> <br/> @gyuri127](https://github.com/gyuri127) |

</div>

<br>

## 1. 개발 환경

- Front : HTML, CSS, JavaScript, React
- Back-end : Spring Boot, JWT, Spring Data JPA, Spring Security, MariaDB, Redis, OAuth2
- 버전 및 이슈관리 : Github, Github Issues, Github Project
- 협업 툴 : Discord, Notion, Gether
- 디자인 : [Figma](https://www.figma.com/file/fAisC2pEKzxTOzet9CfqML/README(oh-my-code)?node-id=39%3A1814)
<br>
<br>

## 2. 프로젝트 구조

```
├── README.md
├── .gitignore
├── build.gradle
├── package-lock.json
├── package.json
│
└── src
     ├── api
     │     └── axios.js
     │
     ├── main.java.com.example.festimo
     │     ├── domain
     │     │         ├── admin
     │     │         │    ├── controller
     │     │         │    ├── dto
     │     │         │    ├── mapper
     │     │         │    └── service
     │     │         ├── festival
     │     │         │    ├── controller
     │     │         │    ├── domain
     │     │         │    ├── dto
     │     │         │    ├── repository
     │     │         │    └── service
     │     │         ├── meet
     │     │         │    ├── controller
     │     │         │    ├── entity
     │     │         │    ├── dto
     │     │         │    ├── mapper
     │     │         │    ├── repository
     │     │         │    └── service
     │     │         ├── oauth
     │     │         │    ├── controller
     │     │         │    ├── dto
     │     │         │    └── service
     │     │         ├── post
     │     │         │    ├── controller
     │     │         │    ├── dto
     │     │         │    ├── entity
     │     │         │    ├── repository
     │     │         │    ├── scheduler
     │     │         │    └── service 
     │     │         ├── review
     │     │         │    ├── controller
     │     │         │    ├── domain
     │     │         │    ├── dto
     │     │         │    ├── repository
     │     │         │    └── service 
     │     │         └── user
     │     │              ├── controller
     │     │              ├── domain
     │     │              ├── dto
     │     │              ├── repository
     │     │              └── service 
     │     ├── exception
     │     │         ├── CustomExcpetion.java
     │     │         ├── ErrorCode.java 
     │     │         └── GlobalExceptionHandler.java 
     │     │                  .
     │     │                  .
     │     └── global
     │               ├── config
     │               │    ├── SecurityConfig.java
     │               │    ├── SwaggerConfig.java
     │               │    └── RedisConfig.java
     │               │        .
     │               │        .
     │               └── utils.jwt
     │                    ├── JwtTokenProvider.java
     │                    ├── JwtAuthenticationFilter.java
     │                    └── OAuth2LoginSuccessHandler.java
     │                        .
     │                        .
     ├── main.resources
     │     ├── properties
     │     │         └── env.properties
     │     ├── static
     │     │         ├── assets
     │     │         │    ├── main.css
     │     │         │    └── main.js
     │     │         ├── components
     │     │         │    ├── common
     │     │         │    ├── community
     │     │         │    ├── home
     │     │         │    └── layout
     │     │         ├── css
     │     │         │    ├── admin.css
     │     │         │    ├── festival.css
     │     │         │    └── login.css 
     │     │         │        .
     │     │         │        .
     │     │         ├── html
     │     │         │    ├── adminPage.html
     │     │         │    ├── festival.html
     │     │         │    └── login.html
     │     │         │        .
     │     │         │        .           
     │     │         ├── imgs
     │     │         │    ├── alt_img.jpg
     │     │         │    └── festimoLogo.svg
     │     │         │        .
     │     │         │        .  
     │     │         ├── js
     │     │         │    ├── admin.js
     │     │         │    ├── festival.js
     │     │         │    └── header.js
     │     │         │        .
     │     │         │        .     
     │     │         └── application.properties   
     └── test.java.com.example.festimo
           ├── admin
           ├── festival
           └── meet
               .
               .
```

<br>

## 4. 역할 분담

### 🍊김태영

- **UI**
    - 페이지 : 마이페이지, 개인 정보 수정, 리뷰 검색, 리뷰 작성
- **기능**
    - 회원가입 : OAuth2 카카오 소셜 로그인 구현, 보안 토큰 발급 로직 설계 및 구현
    - 회원 관리 : AccessToken & RefreshToken 활용한 인증 및 인가 기능
    - 로그인, 로그아웃 : 토큰 만료 시 재발급 등 토큰 관리
    - 리뷰 조회 및 작성 : 회원별 리뷰 조회 및 작성

<br>

### 🐾남주연

- **UI**
    - 페이지 : 축제 살펴보기 페이지, 축제 상세 보기 페이지
- **기능**
    - 축제 살펴보기: 축제 조회, 축제 검색, 축제 연도&월 별 필터링 및 지역 별 필터링
    - 축제 상세보기: 축제 상세 정보, 축제 개최 위치 지도

<br>
    
### 🎀이하연

- **UI**
    - 페이지 : 홈 화면, 게시판 메인, 게시글 작성 페이지, 게시글 수정 페이지, 게시글 상세 페이지, 축제 달력
- **기능**
    - 게시글 및 댓글 관리: 게시글과 댓글의 조회, 등록, 수정, 삭제
    - 상호작용 기능: 게시글 좋아요 기능
    - 추천 및 인기 콘텐츠: 자주 사용된 태그 조회, 인기 게시물 조회
    - 검색 기능: 키워드 기반 게시글 검색, 태그 기반 게시글 검색

<br>

### 😎박현욱

- **UI**
    - 페이지 : 로그인 페이지, 회원가입 페이지
- **기능**
    - 백엔드 : Spring Security 필터 관리, JWT 토큰 발급 및 인증/인가
             OAuth 2.0 소셜 로그인 연동
    - 프론트 : 토큰 저장, 토큰을 사용해 사용자 인증

<br>


### 🐬황규리

- **UI**
    - 페이지 : 동행페이지, 관리자 페이지
- **기능**
    - 동행 : 동행 신청, 동행 신청 리스트 조회, 동행 수락, 동행 거절, 동행 조회, 동행 탈퇴
    - 관리자 : 회원 조회, 회원 수정, 회원 삭제, 게시글 삭제, 리뷰 조회, 리뷰 삭제

    
<br>

## 5. 개발 기간 및 작업 관리

### 개발 기간

- 전체 개발 기간 : 2024-12-09 ~ 2025-01-05
- UI 구현 : 2024-12-21 ~ 2025-01-01
- 기능 구현 : 2024-12-09 ~ 2025-01-02

<br>

### 작업 관리

- GitHub Projects와 Issues를 사용하여 진행 상황을 공유했습니다.
- 주간회의를 진행하며 작업 순서와 방향성에 대한 고민을 나누고 Notion에 회의 내용을 기록했습니다.

<br>

## 6. 프로젝트 후기

### 🍊 김태영

프로젝트를 진행하며 기획 단계의 중요성을 깊이 깨닫는 기간이었습니다. 초기 단계에서의 명확한 계획이 프로젝트의 완성도를 결정짓는다는 점을 실감했습니다.
깃허브 사용이 익숙하지 않아 걱정했지만, 팀원들과 함께 충돌을 해결하며 협업 능력을 키울 수 있었고, Git에 대한 이해도도 높아졌습니다.
처음 맡아보는 팀장 역할이라 부족한 점이 많았지만, 팀원들의 도움 덕분에 무사히 프로젝트를 마무리할 수 있었습니다. 다만, 개인적으로 아쉬웠던 점은 프로젝트 마무리 기간의 부담감으로 인해 트러블슈팅 기록을 남기지 못한 것입니다. 문제 해결 과정을 체계적으로 기록했더라면, 더 큰 배움이 되었을 것이라 생각합니다.
이번 경험을 통해 얻은 교훈을 바탕으로, 다음 프로젝트에서는 더욱 완성도 높은 결과물을 만들어내고 싶습니다.
팀원들 모두 정말 고생 많으셨습니다. 감사합니다.

<br>

### 🐾 남주연

이번 팀 프로젝트를 통해 구현 방법에 대해 깊이 고민하고, 이를 실제로 구현하며 정리하는 과정에서 많은 것을 배울 수 있었습니다. 특히 복잡한 문제를 해결하기 위해 여러 접근 방식을 비교하고 최적의 방법을 선택하는 과정이 저에게 큰 도움이 되었다 생각됩니다. 이번 프로젝트에서는 많은 양의 데이터를 효율적으로 처리할 방법을 고민하고 구현해볼 수 있었던 점이 의미 있었습니다. 
또한, 이번 팀 프로젝트를 통해 구체적이고 명확한 기획이 프로젝트에서 얼마나 중요한지 깨달았습니다. 초기 단계에서 명확한 목표를 설정하고 이를 실현 가능한 세부 계획으로 발전시키는 과정을 어떤 식으로 효율적으로 할 수 있을지 고민할 수 있었던 값진 시간이었습니다. 
협업 과정에서는 팀원들과의 소통과 의견 조율을 통해 프로젝트를 발전시킬 수 있었습니다. 서로의 의견을 다양한 관점을 반영함으로써, 더 나은 결과물을 만들 수 있었습니다. 
하지만 아쉬웠던 점으로는 트러블슈팅 과정을 일일이 기록하지 못한 점과 코드 리팩토링에 충분한 시간을 할애하지 못했다는 것입니다. 이러한 부분은 앞으로 프로젝트를 진행할 때 반드시 개선하고자 합니다. 
같은 목표를 공유하며 협력한 팀원들과 함께 프로젝트를 완수할 수 있었던 점이 매우 뜻깊었습니다. 모든 팀원이 고생한 덕분에 성공적으로 프로젝트를 마무리할 수 있었다 생각합니다. 다들 고생 많으셨습니다!

<br>

### 🎀 이하연

이번 팀 프로젝트를 통해 체계적인 설계의 중요성을 깊이 깨달을 수 있었습니다. 단순히 개발에만 집중하는 것이 아니라 구체적인 기획과 설계가 개발의 방향성과 효율성을 크게 좌우한다는 것을 직접 체감했습니다. 특히, 기획 단계에서의 디테일이 부족했던 점이 아쉬움으로 남습니다. 이로 인해 개발 단계에서 테이블 구조를 수정하거나 팀 내 소통을 다시 조율해야 하는 상황이 발생하기도 했는데. 이를 통해 철저한 기획과 사전 준비가 얼마나 중요한지 배울 수 있었습니다.
그리고 프로젝트 기간 동안 기한 내 기능을 구현하는 데 초점을 맞추다 보니, 트러블슈팅 과정과 새롭게 배운 내용들을 체계적으로 기록하지 못한 점이 개인적으로 아쉬웠습니다. 이러한 점은 앞으로의 개발 과정에서 꼭 개선하고자 합니다. 프로젝트 진행 중 느꼈던 점들을 바탕으로, 이후에는 효율적인 기록과 지식의 체계적인 관리를 통해 성장하는 개발자가 되고 싶습니다.
마지막으로, 연말과 새해 일정 속에서도 끝까지 책임감을 가지고 개발에 힘써주신 팀원 여러분들께 정말 감사드립니다. 모두가 노력해주신 덕분에 프로젝트를 무사히 마무리할 수 있었고, 함께 협력하며 많은 것을 배울 수 있었던 값진 시간이었습니다. 정말 고생 많으셨습니다! 🎉

<br>

### 😎 박현욱

팀 프로젝트 시작에 앞서 초기 설정을 진행하며 체계적인 설계의 중요성을 느꼈습니다. 앞으로는 점점 더 체계적이고 효율적으로 프로젝트를 진행할 수 있도록 발전하고 싶습니다.
정규 수업 직후에 프로젝트를 진행하면서 배운 내용을 직접 구현하는 과정이 어색했지만 어떤 부분이 부족한지 알 수 있는 기회였습니다. 스스로 최대한 노력해보고 팀원들과 함께 해결해 나가면서 협업의 장점을 체감할 수 있었습니다. 하지만 빠르게 작업을 진행하면서 팀원들과 함께 해결한 이슈가 어떤 이슈이며 어떻게 해결했는지에 대해 자세히 작성하지 못한 것이 아쉽습니다.
’멋쟁이 사자처럼’이라는 같은 목표를 가진 집단에서 프로젝트에 함께할 수 있는 소중한 경험이었습니다. 함께 고생한 조원들 모두 고생하셨습니다! 앞으로도 화이팅해서 함께 목표를 이뤄가고 싶습니다.

<br>

### 🐬 지창언

컨벤션을 정하는 것부터 Readme 파일 작성까지 전 과정을 진행하려니 처음 생각보다 많은 에너지를 썼어요. 좋은 의미로 많이 썼다기보다, 제 능력을 십분 발휘하지 못해서 아쉬움이 남는 쪽입니다. 개발한다고 개발만 해서는 안 된다는 것을 몸소 느껴보는 기간이었던 것 같습니다. 이번 기회로 프로젝트를 진행하면서, 제가 잘하는 점과 부족한 점을 확실하게 알고 가는 건 정말 좋습니다. 기술적인 부분에 있어서는 리액트의 컴포넌트화가 주는 장점을 알았습니다. 조금 느린 개발이 되었을지라도 코드 가독성 부분에 있어서 좋았고, 오류가 발생해도 전체가 아닌 오류가 난 컴포넌트와 근접한 컴포넌트만 살펴보면 수정할 수 있는 부분이 너무 편했습니다. 모두 고생 참 많으셨고 리팩토링을 통해 더 나은 프로젝트 완성까지 화이팅입니다.
