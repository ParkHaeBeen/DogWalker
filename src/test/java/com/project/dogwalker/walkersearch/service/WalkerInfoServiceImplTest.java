package com.project.dogwalker.walkersearch.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkersearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo.Response;
import com.project.dogwalker.walkersearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerTimePrice;
import com.project.dogwalker.walkersearch.dto.WalkerUnAvailDetail;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.elasticsearch.common.geo.GeoPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class WalkerInfoServiceImplTest {

  @Mock
  private WalkerSearchRepository walkerSearchRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private WalkerInfoServiceImpl walkerInfoService;

  @Test
  @DisplayName("고객 위치 중심 검색 + 이름 검색 가능")
  void getWalkerInfoList() {
    //given
    MemberInfo memberInfo=MemberInfo.builder()
        .email("info@gmail.com")
        .role(Role.USER)
        .build();
    WalkerInfoSearchCond searchCond=WalkerInfoSearchCond.builder()
        .name("test")
        .lnt(12.0)
        .lat(11.0)
        .build();

    User user= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(11.0)
        .userEmail("info@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("info")
        .userRole(Role.WALKER)
        .build();
    WalkerDocument document1=WalkerDocument.builder()
        .walker_info_id(1L)
        .walker_name("test1")
        .location(new GeoPoint(12.0,11.0))
        .build();
    WalkerDocument document2=WalkerDocument.builder()
        .walker_info_id(2L)
        .walker_name("test2")
        .location(new GeoPoint(12.00001,11.0))
        .build();
    Page <WalkerDocument> pages=new PageImpl <>(Arrays.asList(document1,document2));
    Pageable pageable = PageRequest.of(0,10);

    given(userRepository.findByUserEmailAndUserRole(anyString(),any())).willReturn(Optional.of(user));
    given(walkerSearchRepository.searchByName(any(),any())).willReturn(pages);

    //when
    List <WalkerInfo> walkerInfoList = walkerInfoService.getWalkerInfoList(memberInfo,searchCond,pageable);

    //then
    Assertions.assertThat(walkerInfoList.size()).isEqualTo(2);

  }

  @Test
  @DisplayName("서비스 수행자 안되는 요일.시간 + 일시적으로 안되는 날짜 + 단위시간당 가격 조회")
  void getWalkerUnAvailService() {
    //given
    User user= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerInfo1@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerInfo1")
        .userRole(Role.WALKER)
        .build();
    WalkerPermUnAvailDate permUnAvailDate1=WalkerPermUnAvailDate.builder()
        .dayOfWeak("MON")
        .startTime(3)
        .endTime(5)
        .build();
    WalkerPermUnAvailDate permUnAvailDate2=WalkerPermUnAvailDate.builder()
        .dayOfWeak("TUE")
        .startTime(5)
        .endTime(7)
        .build();
    List<WalkerPermUnAvailDate> permUnAvailDates=List.of(permUnAvailDate1,permUnAvailDate2);

    WalkerTempUnAvailDate tempUnAvailDate1=WalkerTempUnAvailDate.builder()
        .dateTime(LocalDate.of(2023,12,25))
        .build();

    WalkerTempUnAvailDate tempUnAvailDate2=WalkerTempUnAvailDate.builder()
        .dateTime(LocalDate.of(2023,12,24))
        .build();
    List<WalkerTempUnAvailDate> tempUnAvailDates=List.of(tempUnAvailDate1,tempUnAvailDate2);

    WalkerTimePrice walkerTimePrice1=WalkerTimePrice.builder()
        .serviceFee(10000)
        .timeUnit(30)
        .build();
    WalkerTimePrice walkerTimePrice2=WalkerTimePrice.builder()
        .serviceFee(15000)
        .timeUnit(40)
        .build();
    List<WalkerTimePrice> walkerTimePrices=List.of(walkerTimePrice1,walkerTimePrice2);

    given(userRepository.findByUserIdAndUserRole(anyLong(),any())).willReturn(Optional.of(user));
    given(userRepository.walkerPermUnVailScheduleFindByWalkerId(anyLong())).willReturn(permUnAvailDates);
    given(userRepository.walkerTempUnAvailFindByWalkerId(anyLong())).willReturn(tempUnAvailDates);
    given(userRepository.walkerTimePrices(anyLong())).willReturn(walkerTimePrices);

    //when
    WalkerUnAvailDetail walkerUnAvailService = walkerInfoService.getWalkerUnAvailService(1L);

    //then
    Assertions.assertThat(walkerUnAvailService.getWalkerName()).isEqualTo("walkerInfo1");
    Assertions.assertThat(walkerUnAvailService.getPermUnAvailDates().size()).isEqualTo(2);
    Assertions.assertThat(walkerUnAvailService.getTempUnAvailDates().size()).isEqualTo(2);
    Assertions.assertThat(walkerUnAvailService.getTimePrices().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("해당 날짜에 예약이 있는지 확인")
  void getReserveDate() {
    //given
    WalkerReserveInfo.Request request=WalkerReserveInfo.Request.builder()
        .walkerId(1L)
        .checkReserveDate(LocalDate.of(2023,12,15))
        .build();
    WalkerReserveInfo.Response response1=WalkerReserveInfo.Response.builder()
        .reserveDate(LocalDateTime.of(2023,12,15,15,0))
        .build();
    WalkerReserveInfo.Response response2=WalkerReserveInfo.Response.builder()
        .reserveDate(LocalDateTime.of(2023,12,15,16,0))
        .build();

    List<WalkerReserveInfo.Response> responses=List.of(response1,response2);

    given(userRepository.walkerReserveDate(anyLong(),any())).willReturn(responses);
    //when
    List <Response> reserveDates = walkerInfoService.getReserveDate(request);

    //then
    Assertions.assertThat(reserveDates.size()).isEqualTo(2);
  }
}