package com.project.dogwalker.domain.user.walker;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_ACCEPT;

import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.walkersearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo.Response;
import com.project.dogwalker.walkersearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerTimePrice;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
class WalkerInfoRepositoryImplTest {


  @Autowired
  private UserRepository userRepository;
  @Autowired
  private WalkerScheduleRepository walkerScheduleRepository;
  @Autowired
  private WalkerScheduleTempRepository walkerScheduleTempRepository;
  @Autowired
  private WalkerServicePriceRepository walkerServicePriceRepository;
  @Autowired
  private WalkerReserveServiceRepository walkerReserveServiceRepository;


  @Test
  @Rollback
  @DisplayName("서비스 예약 아예안되는 시간 조회- 성공")
  void queryDsl_walkerInfo_perunavail_succes(){
    //given
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("query1")
        .userRole(Role.WALKER)
        .build();

    User saveWalker = userRepository.save(walker);
    WalkerSchedule schedule1=WalkerSchedule.builder()
        .walkerId(saveWalker.getUserId())
        .startTime(13)
        .endTime(18)
        .dayOfWeek("mon")
        .build();
    WalkerSchedule schedule2=WalkerSchedule.builder()
        .walkerId(saveWalker.getUserId())
        .startTime(15)
        .endTime(18)
        .dayOfWeek("tue")
        .build();
    walkerScheduleRepository.save(schedule1);
    walkerScheduleRepository.save(schedule2);


    //when
    List <WalkerPermUnAvailDate>  WalkerPermUnAvailDate = userRepository.walkerPermUnVailScheduleFindByWalkerId(saveWalker.getUserId());

    //then
    Assertions.assertThat(WalkerPermUnAvailDate.size()).isEqualTo(2);
  }


  @Test
  @Rollback
  @DisplayName("서비스 예약 일시적으로 안되는 날짜 조회- 성공")
  void queryDsl_walkerInfo_tempunavail_succes(){
    //given
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query2@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("querㅛ2")
        .userRole(Role.WALKER)
        .build();

    User saveWalker = userRepository.save(walker);

    WalkerScheduleTemp tempSchedule1=WalkerScheduleTemp.builder()
        .walkerId(saveWalker.getUserId())
        .dateTime(LocalDate.of(2023,12,25))
        .build();
    WalkerScheduleTemp tempSchedule2=WalkerScheduleTemp.builder()
        .walkerId(saveWalker.getUserId())
        .dateTime(LocalDate.of(2023,12,25))
        .build();
    walkerScheduleTempRepository.save(tempSchedule1);
    walkerScheduleTempRepository.save(tempSchedule2);

    //when
    List <WalkerTempUnAvailDate>  WalkerTempUnAvailDate = userRepository.walkerTempUnAvailFindByWalkerId(saveWalker.getUserId());

    //then
    Assertions.assertThat(WalkerTempUnAvailDate.size()).isEqualTo(2);
  }

  @Test
  @Rollback
  @DisplayName("서비스 수행자 시간단위당 비용검색- 성공")
  void queryDsl_walkerInfo_price_search(){
    //given
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query3@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("query3")
        .userRole(Role.WALKER)
        .build();

    User saveWalker = userRepository.save(walker);

    WalkerServicePrice walkerServicePrice1=WalkerServicePrice.builder()
        .serviceFee(10000)
        .timeUnit(30)
        .walkerId(saveWalker.getUserId())
        .build();

    WalkerServicePrice walkerServicePrice2=WalkerServicePrice.builder()
        .serviceFee(10000)
        .timeUnit(30)
        .walkerId(saveWalker.getUserId())
        .build();

    walkerServicePriceRepository.save(walkerServicePrice1);
    walkerServicePriceRepository.save(walkerServicePrice2);

    //when
    List <WalkerTimePrice>  WalkerTimePrice = userRepository.walkerTimePrices(saveWalker.getUserId());

    //then
    Assertions.assertThat(WalkerTimePrice.size()).isEqualTo(2);
  }

  @Test
  @Rollback
  @DisplayName("해당 날짜 예약된 날짜+시간 보내기")
  void walkerReserveDate(){
    //given
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query4@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("query4")
        .userRole(Role.WALKER)
        .build();

    User customer= User.builder()
        .userId(2L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query6@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("query6")
        .userRole(Role.USER)
        .build();
    User walker2= User.builder()
        .userId(3L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("query5@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("query5")
        .userRole(Role.WALKER)
        .build();

    User saveWalker = userRepository.save(walker);
    User saveCustomer = userRepository.save(customer);
    User saveWalker2 = userRepository.save(walker2);
    WalkerReserveServiceInfo serviceInfo1=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,15,16,0))
        .walker(walker)
        .customer(saveCustomer)
        .servicePrice(10000)
        .timeUnit(30)
        .status(WALKER_ACCEPT)
        .build();
    WalkerReserveServiceInfo serviceInfo2=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,15,18,0))
        .walker(walker)
        .customer(saveCustomer)
        .servicePrice(10000)
        .timeUnit(30)
        .status(WALKER_ACCEPT)
        .build();
    WalkerReserveServiceInfo serviceInfo3=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,15,12,0))
        .walker(walker2)
        .customer(saveCustomer)
        .servicePrice(10000)
        .timeUnit(30)
        .status(WALKER_ACCEPT)
        .build();
    walkerReserveServiceRepository.save(serviceInfo1);
    walkerReserveServiceRepository.save(serviceInfo2);
    walkerReserveServiceRepository.save(serviceInfo3);
    //when
    List <Response> responses = userRepository.walkerReserveDate(saveWalker.getUserId() ,
        LocalDate.of(2023 , 12 , 15));
    //then
    Assertions.assertThat(responses.size()).isEqualTo(2);
  }
}