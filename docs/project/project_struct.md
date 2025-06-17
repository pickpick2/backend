# 프로젝트의 디렉토리 구조
```
docker                                  도커 이미지 디렉토리
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