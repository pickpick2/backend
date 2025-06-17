# PicPic Backend
PicPic 프로젝트의 백엔드 레포지토리 입니다.

# Directory Struct
```
docker                                          도커 이미지 디렉토리
src
├── main
│   ├── java
│   │   └── com
│   │       └── picpic
│   │           └── server
│   │               ├── ServerApplication.java
│   │               ├── common                  공통 디렉토리
│   │               │   ├── config              설정    
│   │               │   ├── exception           예외
│   │               │   ├── filter              필터  
│   │               │   └── response            응답 객체
│   │               ├── member                  회원 도메인
│   │               │   ├── controller      
│   │               │   ├── dto
│   │               │   ├── entity
│   │               │   ├── repository
│   │               │   └── service
│   │               └── room                    방 소켓 관련 도메인
│   │                   ├── controller
│   │                   ├── dto
│   │                   ├── entity
│   │                   ├── repository
│   │                   └── service
│   └── resources
│       └── application.yaml
└── test
└── java
└── com
└── picpic
└── server
└── ServerApplicationTests.java
```

# 개발환경
## version
java: 17  
spring boot: 3.5.0  
mysql: 8.0

### DB
#### 운영환경
mysql: 8.0  
redis:latest

#### 개발환경
h2(in-memory)  
redis:latest  

# docs 링크 목록
- [project](./docs/project)
  - [project(프로젝트 정보)](./docs/project/project.md)
  - [project_init(프로젝트 세팅 정보)](./docs/project/project_init.md)
  - [project_struct(프로젝트 구조)](./docs/project/project_struct.md)
