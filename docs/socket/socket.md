> Author: jungchanSon  
> Date: 2025.06.25 

Web Socket 설정에 대한 문서입니다.

# STOMP
STOMP(Simple Text Oriented Messaging Protocol) 서브 프로토콜을 사용하고 있습니다.

# EndPoints
1. Connection:   
    `/wss/connection`
2. subscribe prefix:  
    `/topic`
3. publish prefix:  
    `/app`

# 연결 작업
클라이언트의 웹 소켓 연결에 대한 관리는 redis로 관리하고 있습니다.

### 웹 소켓 최초 연결시
웹 소켓에 최초 연결 시, 아래의 데이터들이 redis에 저장됩니다.

| Key                           | Value         |
|-------------------------------|---------------|
| sessionId:{sessionId}:roomId  | roomId        |
| sessionId:{sessionId}:userName | userName      |
| room:{roomId}:sessionIds      | [...sessionId] |
| room:{roomId}:userNames      | [...userName] |

### 웹 소켓 연결 해제시
위 소켓 연결시 저정한 데이터들을 삭제합니다.