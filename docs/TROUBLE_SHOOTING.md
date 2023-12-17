# Trouble Shooting

### 1. @FeignClient 파리미터 문제

문제 상황
- 네이버 로그인 연동결과로 code값을 받았지만 접근 토큰 발급 요청에서 Response객체의 필드가 다 null인 상황

문제 해결
- @SpringQueryMap 을 붙여줌으로써 Request 객체 필드들이 쿼리 매개변수로 사용하게함

상세 링크

[블로그 - Openfeign 파라미터](https://haebing.tistory.com/94)

<br>

### 2. aop 분산락과 트랜젝션 문제

문제 상황
- 동시성 테스트 중 여러 스레드가 동시에 redis락을 획득함
- insert가 2번되는 상황 발생

문제 해결
- service쪽에 붙여있던 @Transactional 제거

상세링크

[블로그 - aop 분산락과 트랜젝션](https://haebing.tistory.com/100)

<br>

### 3. Redis 역직렬화 문제

문제 상황
- Coordinate 객체를 저장하던중 ClassCastException,JsonMappingException 예외 발생 

문제 해결
- Redis역직렬화 방법 변경 및 직렬/역직렬변환기 생성

상세링크

[블로그 - Redis 역직렬화 문제](https://haebing.tistory.com/105)

<br>

### 4. OneToOne관계 N+1문제

문제 상황
- OneToOne관계를 가진 테이블을 조회하던중 N+1문제 발견

문제 해결
- 연관관계 (부모,자식) 관계 다시 설정

상세링크

[블로그 - OneToOne N+1 문제](https://haebing.tistory.com/106)
