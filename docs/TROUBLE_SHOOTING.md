# Trouble Shooting

### 1. @FeignClient 파리미터 문제

문제 상황
- 네이버 로그인 연동결과로 code값을 받았지만 접근 토큰 발급 요청에서 Response객체의 필드가 다 null인 상황

문제 해결
- @SpringQueryMap 을 붙여줌으로써 Request 객체 필드들이 쿼리 매개변수로 사용하게함

상세 링크

[블로그 - Openfeign 파라미터](https://haebing.tistory.com/94)
