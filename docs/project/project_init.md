# 설치 필요 목록
- java 17
- docker

# h2 console 실행 (개발용 DB 콘솔)
1. spring application 실행
2. localhost:8080/h2 이동
3. db-url을 `jdbc:h2:mem:picpic`로 수정 후 실행

# redis, mysql 실행
/docker/docker-compose.yml 파일 실행
1. /docker 디렉토리로 이동
1. 아래 명령어 실행
```bash
  docker-compose up -d
```
