# :dog2: 산책 대행 서비스

강아지 산책 알바 예약 서비스입니다.

## 프로젝트 기능 및 설계

### [회원가입과 로그인]
:bulb: 회원가입
- OAuth(google), Email을 통해서 회원가입
- 서비스 이용 고객과 서비스 수행자를 role로서 구분
- 상세 정보를 저장할 테이블 각각 생성(customer_info, walker_info)
  -  서비스 이용 고객
     -  회원가입시 강아지 사진과 함께 등록(강아지 이름, 나이, 사진, 종) -> 이미지 등록 s3이용
     -  DB에 회원 주소 저장 -> 주소 기준으로 서비스 수행자 리스트 보여주기 위해

  - 서비스 수행자
     - 서비스 수행이 불가능한 요일과 시간 저장 (불가능한 시간에 대해서는 시간 단위로 받기)
     - 수행자 자신이 기준이 될 주소 저장 -> 서비스 수행자 리스트때 사용
   

💡 로그인
- JWT 토큰 기반으로 구현
  - (AccessToken + RefreshToken) -> Redis 활용
  - RememberMeAuthentication 구현
 

<br>

### [산책 서비스 수행자 리스트, 선택, 이용 방법]

💡 산책 서비스 수행자 리스트 보여주기
- 서비스 이용 고객 DB 주소 기준으로 반경 1km이내의 산책 서비스 수행자 리스트 보여주기
  - 기본적으로 별점순, 찜순으로 보여주되 고객이 필터 선택시 해당 기준으로 볼수 있음(필터 : 거리순, 별점순, 찜순)
  - 서비스 이용 고객은 검색창에 다른 주소로 산책 서비스 수행자 리스트 검색 가능
    
  -> ElasticSearch 이용하여 구현

💡 산책 서비스 수행자 상세 정보 페이지
- 산책 서비스 수행자의 이름, 위치(수행자 자신이 설정한 주소), 간단한 소개
- 예약가능한 날짜(예약불가능한 요일,시간을 제외한 날짜 + 서비스 수행이 안되는 날짜 제외)
- 기본적으로 1시간 단위로 예약가능시간을 보여주되 서비스 이용시간은 30분,40분,50분 단위로 가능
   -> 만약 오후 1시에 30분단위 예약이 들어오면 1시 30분에 예약 받는 것 불가(2시부터 예약받을 수 있음)

💡 산책 서비스 예약
- 동시성 문제에 대해서 분산락으로 해결 예정

💡 산책 서비스 결제
- 포인트말고 바로 결제 진행

💡 산책 서비스 취소
- 하루 전까지 산책 서비스 취소 가능

<br>

### [산책 서비스 수행자 서비스 수행 요청]

💡 산책 서비스 수행자 서비스 예약에 대해 수락 또는 거부가능
- 서비스 예약 요청에 대한 알람
- 10분 안에 수락또는 거부하지 않으면 자동으로 거부됨
- 수락또는 거부가 되면 해당 고객에게 알람 발송("요청이 수락(겨절)되었습니다" 와 같이)

->  알람기능은 SSE를 이용 

->  10분후 자동 거부 기능은 메세지큐이용

<br>

### [산책 서비스 수행]

💡 산책 서비스 수행중 서비스 이용 고객에게 수행자의 위치 실시간으로 전송
- 실시간 보여줌 + 나중에 내역확인시 산책 경로를 보여주기 위함
-> 실시간 서비스 : 클라이언트 쪽에서 주기적으로 api 호출하는 형태 (api 호출시 위치도 redis이용하여 저장한후 서비스 종료 후 db에 저장)
  
-> 내역 산책 경로 : Mysql muiltipoint 활용

💡 예약시간 단위에따라 5분전에 산책 서비스 수행자에게 알람("서비스 종료 5분전입니다. 고객에게 알려주세여")
- 서비스 수행자는 해당 알람을 받으면 고객에게 "5분뒤 나와주세요"와 같이 알람 보낼 수 있는 기능 추가
-> SSE 이용


<br>

### [산책 서비스 리뷰]

💡 산책 서비스 수행 종료후 해당 산책 서비스 수행자에 대한 리뷰 남길 수 있음


<br>

### [산책 서비스 수행자 정산]

💡 매월말마다 포인트에 대한 정산이 이루어짐
- 스프링 배치 이용


<br>

### [추가 편의 기능]

💡 각 소비자가 자주 이용하는 산책 서비스 수행자 보여주기

💡 소비자와 산책 수행자간의 채팅 서비스
-> WebSocket 이용

<hr>

### ERD

![image](https://github.com/ParkHaeBeen/DogWalker/assets/130157565/515c2838-c1f8-41ce-960d-7c3077d78bf3)



<hr>

# Trouble Shooting

[go to the trouble shooting section](https://github.com/ParkHaeBeen/DogWalker/blob/main/docs/TROUBLE_SHOOTING.md)

<hr>

# Tech Stack

<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/junit5-25A162?style=for-the-badge&logo=junit5&logoColor=white">
  <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
</div>

