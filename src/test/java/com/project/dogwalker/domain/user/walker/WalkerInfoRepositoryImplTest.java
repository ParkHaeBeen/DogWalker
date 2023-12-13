package com.project.dogwalker.domain.user.walker;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.walkerSearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerTimePrice;
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
        .dateTime(LocalDateTime.of(2023,12,25,0,0))
        .build();
    WalkerScheduleTemp tempSchedule2=WalkerScheduleTemp.builder()
        .walkerId(saveWalker.getUserId())
        .dateTime(LocalDateTime.of(2023,12,25,0,0))
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
}