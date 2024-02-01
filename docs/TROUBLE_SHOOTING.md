# 🔥 Trouble Shooting 🔥

## 1. @FeignClient 파리미터 문제

문제 상황
- 네이버 로그인 연동결과로 code값을 받았지만 접근 토큰 발급 요청에서 Response객체의 필드가 다 null인 상황

문제 해결
- @SpringQueryMap 을 붙여줌으로써 Request 객체 필드들이 쿼리 매개변수로 사용하게함

상세 링크

[블로그 - Openfeign 파라미터](https://haebing.tistory.com/94)

<br>

## 2. aop 분산락과 트랜젝션 문제

문제 상황
- Redisson을 이용해 동시성 문제를 해결하려고 했지만 데이터 삽입이 한번 이루어져야 부분이 여러번 되는 문제 발생
- Redisson lock이 정확하게 걸리지 않아 lock 획득하지 않은채로 서비스 코드 트랜젝션을 시작하는 문제 발생

문제 해결
- 동일한 트랜젝션 레벨로 인한 문제로 분산락에 이용되는 서비스 메서드 @Transactional 를 제거

상세링크

[블로그 - aop 분산락과 트랜젝션](https://haebing.tistory.com/100)

<br>

## 3. Redis 역직렬화 문제

문제 상황
- 서비스 수행중 이동경로를 Redis에 저장하여 서비스 수행 완료후 저장하지만 Coordinate가 cast되지 않는 문제 발생
- cast문제 해결후 jsonMappingException으로 직렬화/역직렬화 되지 않은 문제 발생

문제 해결
- Coordinate 필드 하나가 직렬/역직렬화시 에러를 발생시킴
    - StdDeserializer 클래스를 상속하여 직접 직렬/역직렬화 변환기 생성
      
상세링크

[블로그 - Redis 역직렬화 문제](https://haebing.tistory.com/105)

<br>

## 4. OneToOne관계 N+1문제

문제 상황
- 정산하기 위해 예약 테이블과 결제 테이블 fetch join + lazy 모드로 설정하였지만 결제테이블이 N개 만큼 생성
  
문제 해결
- fetch join 정의를 통해 연관관계 주인이 아닌 곳에서 호출하면 즉시로딩된다는 사실 발견
    - 연관관계 주인 쪽에서 fetch join을 하는 쿼리로 수정

상세링크

[블로그 - OneToOne N+1 문제](https://haebing.tistory.com/106)

<br>

## 5. CascadeType 미설정

문제 상황
- 연관관계에 있는 엔티티의 상태 필드가 바뀌어야하지만 update쿼리가 나가지 않는 문제 발생
- 
문제 해결
- casacadeType미설정으로 update 쿼리가 나가지 않음을 발견
    - CascadeType merge로 설정 → Persist의 경우 이미 영속성 컨텍스트에서 분리된 엔티티로 인해 에러가 발생하여 merge로 설정
      
상세링크

[블로그 - cascade 타입](https://haebing.tistory.com/107)

<br>

## 6. 영속성 컨텍스트와 프록시

문제 상황
-  테스트 코드 검증 수행중 엔티티와 연관관계에 있는 다른 엔티티가 lazyInitializationException 발생
- 트랜젝션 어노테이션을 통해 세션을 유지시키려고 했지만 트랜젝션이 다르고 레벨 차이로 인해 별개의 영속성 컨텍스트로 조회되지 않는 문제 발생

문제 해결
- 테스트 코드 검증이기에 Repository에 새로운 조회 메서드를 만드는 것보다는 entityManager를 통해 fetch join하여 데이터 조회

상세링크

[블로그 - 영속성 컨텍스트와 프록시](https://haebing.tistory.com/108)

<br>

## 7. Spring batch Paging Reader에서 같은 조건 데이터 조회안되는 문제
문제 상황
- JpaPagingItemReader를 이용하여 같은 조건의 데이터를 조회 진행
- 데이터 20개로 임시 테스트시 10개만 데이터 값이 변경됨
  
문제 해결
- 변경된 값을 제외하고 조회되지만 Paging 자체의 offset은 증가하기 때문에 데이터가 조회되지 않음
    - 값이 변경될때마다 대상 데이터 범위가 줄어드는 문제
- PagingReader를 override하여 page 번호를 0으로 고정하여 데이터 조회

상세링크

[블로그 - Spring batch paging Reader 문제](https://haebing.tistory.com/143)

<br>

## 8. Github actions build에러
문제 상황
- application.properties가 인식이 안되는 문제 발생
- repository 테스트 코드를 위한 임시 데이터베이스 필요
  
문제 해결
- 테스트 코드용 h2인메모리 데이터베이스 이용
    - 테스트 용 schema.sql파일 생성 → spring batch관련 create table이 다르기 때문
- 테스트 코드용 [application-test.properties](http://application-test.properties) 생성
- 테스트 코드에 @ActiveProfiles 설정하여 application-test.properties적용되도록 수정
  
상세링크

[블로그 - github actions 빌드 에러](https://haebing.tistory.com/144)
