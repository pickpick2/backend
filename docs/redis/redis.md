> Author: jungchanSon  
> Date: 2025.06.25

Redis 설정에 대한 문서입니다.

# RedisTemplate
`GenericJackson2JsonRedisSerializer`을 Serializer로 설정해, JSON 형식의 데이터로 직렬화하여 관리하도록 하였습니다.

# RedisMessageListenerContainer
추후, 스케일-아웃을 고려하여, Redis Listener Container를 Bean으로 등록하였습니다.