package com.project.dogwalker.walkersearch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.support.ControllerTest;
import com.project.dogwalker.walkersearch.dto.WalkerInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkersearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkersearch.dto.WalkerTimePrice;
import com.project.dogwalker.walkersearch.dto.WalkerUnAvailDetail;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class WalkerInfoControllerTest extends ControllerTest {

  @Test
  @DisplayName("회원 위치 기준으로 walker list 검색")
  void getWalkerInfoList() throws Exception{
    //given
    String authorization ="Bearer Token";
    MemberInfo memberInfo=MemberInfo.builder()
        .email("userInfo@gmail.com")
        .role(Role.USER)
        .build();

    WalkerInfoSearchCond cond=WalkerInfoSearchCond.builder()
        .walkerName("walker1")
        .lat(12.0)
        .lnt(11.0)
        .build();

    WalkerInfo info1=WalkerInfo.builder()
        .walkerLat(12.0)
        .walkerLnt(3.0)
        .walkerName("walker1")
        .build();

    WalkerInfo info2=WalkerInfo.builder()
        .walkerLat(12.0)
        .walkerLnt(3.0)
        .walkerName("walker2")
        .build();
    List <WalkerInfo> walkerInfos=List.of(info2,info1);
    Pageable pageable = PageRequest.of(0,10);
    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(walkerInfoService.getWalkerInfoList(memberInfo,cond,pageable)).willReturn(walkerInfos);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/walkerinfo/list")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(cond))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("서비슷 수행자 예약 불가능한 날짜 조회")
  void getWalkerDetail() throws Exception{
    //given
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

    WalkerUnAvailDetail unAvailDetails=WalkerUnAvailDetail.builder()
        .walkerName("walker1")
        .lnt(12.0)
        .lat(3.0)
        .permUnAvailDates(permUnAvailDates)
        .tempUnAvailDates(tempUnAvailDates)
        .timePrices(walkerTimePrices)
        .build();

    given(walkerInfoService.getWalkerUnAvailService(anyLong())).willReturn(unAvailDetails);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/walkerinfo/detail")
            .content(objectMapper.writeValueAsString(1L))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.walkerName").value("walker1"))
        .andExpect(jsonPath("$.lat").value(3.0))
        .andExpect(jsonPath("$.lnt").value(12.0))
        .andExpect(jsonPath("$.permUnAvailDates.size()").value(2))
        .andExpect(jsonPath("$.tempUnAvailDates.size()").value(2))
        .andDo(print());
  }

  @Test
  @DisplayName("해당 날짜에 예약이 있는 날짜가 있는지 조회")
  void getWalkerAlreadyReserveDate() throws Exception{
    //given
    WalkerReserveInfo.Request request=WalkerReserveInfo.Request.builder()
        .checkReserveDate(LocalDate.of(2023,12,25))
        .walkerId(1L)
        .build();

    WalkerReserveInfo.Response response1=WalkerReserveInfo.Response.builder()
        .reserveDate(LocalDateTime.of(2023,12,25,12,0))
        .build();

    WalkerReserveInfo.Response response2=WalkerReserveInfo.Response.builder()
        .reserveDate(LocalDateTime.of(2023,12,25,15,0))
        .build();
    List<WalkerReserveInfo.Response> responses=List.of(response1,response2);

    given(walkerInfoService.getReserveDate(any())).willReturn(responses);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/walkerinfo/detail/check/reserve")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andDo(print());
  }
}