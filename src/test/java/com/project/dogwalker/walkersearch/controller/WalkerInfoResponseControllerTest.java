package com.project.dogwalker.walkersearch.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.dogwalker.support.ControllerTest;
import com.project.dogwalker.walkersearch.dto.response.WalkerInfoResponse;
import com.project.dogwalker.walkersearch.dto.request.WalkerInfoRequest;
import com.project.dogwalker.walkersearch.dto.response.WalkerPermUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.response.WalkerTempUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerTimePriceResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerUnAvailDetailResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class WalkerInfoResponseControllerTest extends ControllerTest {

  @Test
  @DisplayName("회원 위치 기준으로 walker list 검색")
  void getWalkerInfoList() throws Exception{
    //given
    String authorization ="Bearer Token";
    WalkerInfoRequest cond= WalkerInfoRequest.builder()
        .name("walker1")
        .lat(12.0)
        .lnt(11.0)
        .build();

    WalkerInfoResponse info1= WalkerInfoResponse.builder()
        .id(1L)
        .walkerLat(12.0)
        .walkerLnt(11.0)
        .walkerName("walker1")
        .build();

    WalkerInfoResponse info2= WalkerInfoResponse.builder()
        .id(2L)
        .walkerLat(12.0)
        .walkerLnt(11.0)
        .walkerName("walker2")
        .build();

    List <WalkerInfoResponse> walkerInfoResponses =List.of(info2,info1);
    given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
    given(walkerInfoService.getWalkerInfoList(any(),any(),any())).willReturn(walkerInfoResponses);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/walkerinfo/list")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .queryParam("name",cond.getName())
            .queryParam("lnt",String.valueOf(cond.getLnt()))
            .queryParam("lat",String.valueOf(cond.getLat()))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("walkerInfo/list",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())
            ));
  }

  @Test
  @DisplayName("서비슷 수행자 예약 불가능한 날짜 조회")
  void getWalkerDetail() throws Exception{
    //given
    WalkerPermUnAvailDateResponse permUnAvailDate1= WalkerPermUnAvailDateResponse.builder()
        .dayOfWeak("MON")
        .startTime(3)
        .endTime(5)
        .build();
    WalkerPermUnAvailDateResponse permUnAvailDate2= WalkerPermUnAvailDateResponse.builder()
        .dayOfWeak("TUE")
        .startTime(5)
        .endTime(7)
        .build();
    List<WalkerPermUnAvailDateResponse> permUnAvailDates=List.of(permUnAvailDate1,permUnAvailDate2);

    WalkerTempUnAvailDateResponse tempUnAvailDate1= WalkerTempUnAvailDateResponse.builder()
        .dateTime(LocalDate.of(2023,12,25))
        .build();

    WalkerTempUnAvailDateResponse tempUnAvailDate2= WalkerTempUnAvailDateResponse.builder()
        .dateTime(LocalDate.of(2023,12,24))
        .build();
    List<WalkerTempUnAvailDateResponse> tempUnAvailDates=List.of(tempUnAvailDate1,tempUnAvailDate2);

    WalkerTimePriceResponse walkerTimePriceResponse1 = WalkerTimePriceResponse.builder()
        .serviceFee(10000)
        .timeUnit(30)
        .build();
    WalkerTimePriceResponse walkerTimePriceResponse2 = WalkerTimePriceResponse.builder()
        .serviceFee(15000)
        .timeUnit(40)
        .build();
    List<WalkerTimePriceResponse> walkerTimePriceResponses =List.of(walkerTimePriceResponse1 ,
        walkerTimePriceResponse2);

    WalkerUnAvailDetailResponse unAvailDetails= WalkerUnAvailDetailResponse.builder()
        .walkerName("walker1")
        .lnt(12.0)
        .lat(3.0)
        .permUnAvailDates(permUnAvailDates)
        .tempUnAvailDates(tempUnAvailDates)
        .timePrices(walkerTimePriceResponses)
        .build();

    given(walkerInfoService.getWalkerUnAvailService(anyLong())).willReturn(unAvailDetails);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/walkerinfo")
            .param("walkerId","1")
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.walkerName").value("walker1"))
        .andExpect(jsonPath("$.lat").value(3.0))
        .andExpect(jsonPath("$.lnt").value(12.0))
        .andExpect(jsonPath("$.permUnAvailDates.size()").value(2))
        .andExpect(jsonPath("$.tempUnAvailDates.size()").value(2))
        .andDo(print())
        .andDo(document("walkerInfo/unavail",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())
        ));;
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
        get("/walkerinfo/reserve")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andDo(print())
        .andDo(document("walkerInfo/reserve",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())
        ));;
  }
}