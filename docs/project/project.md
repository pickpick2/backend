# 개발환경
## version
java: java 17  
spring: 3.5.0  

## DB
### 운영환경  
mysql: 8.0  
redis:latest

### 개발환경
h2(in-memory)  
redis:latest

## docker - 개발 환경
- /docker/docker-compose.yml에서 개발 환경 container 관리
- redis, mysql 등 컨테이너에 대한 불륨은 하위 디렉토리에서 바인딩하여 관리
  - 예시)
    - 컨테이너 내 `redis.conf` --> `/docker/redis/conf` 에 바인딩
    - 컨테이너 내 data는 각자의 로컬 개발 진행에 따라 상이할 수 있으므로, `.gitignore` 처리함
    - 자세한 바인딩 정보는 [docker-compose.yml](../../docker/docker-compose.yml) 내부 volumes 설정 참조