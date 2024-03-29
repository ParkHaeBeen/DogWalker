# ⚙️ 프로젝트 기능 및 설계

<div align=center>
<img src=https://github.com/ParkHaeBeen/DogWalker/assets/130157565/421e21bd-db09-4dda-8e77-0321b9113890 width=600>
</div>

### 1. 회원가입과 로그인
:bulb: 회원가입 
- OAuth(google,naver)을 통해서 회원가입
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
  - AccessToken -> 만료시 RefreshToken으로 AccessToken 재지급
  - RefreshToken -> 쿠키 + db이용 + 쿠키 만료시 프론트단에서 요청

<br>

### 2. 산책 서비스 수행자 리스트, 선택, 이용 방법

💡 산책 서비스 수행자 리스트 보여주기
- 서비스 이용 고객 DB 주소 기준으로 반경 3km이내의 산책 서비스 수행자 리스트 보여주기
  - 서비스 이용 고객은 검색창에 다른 주소로 산책 서비스 수행자 리스트 검색 가능
    
  -> ElasticSearch 이용하여 구현

💡 산책 서비스 수행자 상세 정보 페이지
- 산책 서비스 수행자의 이름, 위치(수행자 자신이 설정한 주소), 간단한 소개
- 예약가능한 날짜(예약불가능한 요일,시간을 제외한 날짜 + 서비스 수행이 안되는 날짜 제외(ex. 12월 31일 ))
- 기본적으로 1시간 단위로 예약가능시간을 보여주되 서비스 이용시간은 30분,40분,50분 단위로 가능
   -> 만약 오후 1시에 30분단위 예약이 들어오면 1시 30분에 예약 받는 것 불가(2시부터 예약받을 수 있음)

💡 산책 서비스 예약
- 동시성 문제에 대해서 Redisson 분산락으로 해결 

💡 산책 서비스 취소
- 하루 전까지 산책 서비스 취소 가능

<br>

### 3. 산책 서비스 수행자 서비스 수행 요청

💡 산책 서비스 수행자 서비스 예약에 대해 수락 또는 거부가능
- 서비스 예약 요청에 대한 알람
- 10분 안에 수락또는 거부하지 않으면 자동으로 거부됨
- 수락또는 거부가 되면 해당 고객에게 알람 발송("요청이 수락(겨절)되었습니다" 와 같이)

->  알람기능은 SSE를 이용 
->  10분후 자동 거부 기능은 스프링 배치 이용

<br>

### 4. 산책 서비스 수행

💡 산책 서비스 수행중 서비스 이용 고객에게 수행자의 위치 실시간으로 전송
- 내역확인시 산책 경로를 보여주기
-> 실시간 서비스 : 클라이언트 쪽에서 주기적으로 api 호출하는 형태 (api 호출시 위치도 redis이용하여 저장한후 서비스 종료 후 db에 저장)
  
-> 내역 산책 경로 : Mysql muiltipoint 활용

💡 예약시간 단위에따라 5분전에 산책 서비스 수행자에게 알람("서비스 종료 5분전입니다. 고객에게 알려주세여")
- 서비스 수행자는 해당 알람을 받으면 고객에게 "5분뒤 나와주세요"와 같이 알람 보낼 수 있는 기능 추가
-> SSE 이용

<br>


### 5. 산책 서비스 수행자 정산

💡 매월말마다 포인트에 대한 정산이 이루어짐
- 스프링 배치 이용


