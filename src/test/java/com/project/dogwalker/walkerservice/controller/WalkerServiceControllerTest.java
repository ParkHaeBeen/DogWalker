package com.project.dogwalker.walkerservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.common.config.WebConfig;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.walkerservice.dto.RealTimeLocation;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;
import com.project.dogwalker.walkerservice.service.WalkerServiceImpl;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(WalkerServiceController.class)
@Import(WebConfig.class)
class WalkerServiceControllerTest {

  @MockBean
  private WalkerServiceImpl walkerService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;


  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("예약이 존재하는지, walker가 해당 예약을 수행하는게 맞는지, 날짜 확인 ")
  void checkReserveAndWalker() throws Exception {
    //given
    String authorization ="Bearer Token";

    ServiceCheckRequest request=ServiceCheckRequest.builder()
        .nowDate(LocalDateTime.now().minusMinutes(10))
        .reserveId(1L)
        .build();


    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.isWalker(authorization)).willReturn(true);
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/service/check/valid")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("고객이 산책 서비스가 시작되었는지 확인 - 시작됨")
  void checkServiceStart_success() throws Exception {
    //given
    String authorization ="Bearer Token";

   Long reserveId=1L;

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(walkerService.isStartedService(reserveId)).willReturn(true);
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/service/check/start")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(reserveId))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("고객이 산책 서비스가 시작되었는지 확인 - 시작되지 않음")
  void checkServiceStart_fail() throws Exception {
    //given
    String authorization ="Bearer Token";

    Long reserveId=1L;

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(walkerService.isStartedService(reserveId)).willReturn(false);
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/service/check/start")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(reserveId))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("서비스 수행자 위치 저장")
  void realTimeLocation() throws Exception {
    //given
    RealTimeLocation location=RealTimeLocation.builder()
        .lat(12.0)
        .lnt(2.0)
        .reserveId(1L)
        .build();

    //when
    ResultActions resultActions = mockMvc.perform(
        post("/api/service/location")
            .content(objectMapper.writeValueAsString(location))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    verify(walkerService).saveRealTimeLocation(location);
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("고객에게 산책 서비스 완료 5분전 알림 - 성공")
  void noticeCustomer() throws Exception{
    //given
    String authorization ="Bearer Token";

    given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
    given(jwtTokenProvider.isWalker(anyString())).willReturn(true);

    //when
    ResultActions resultActions = mockMvc.perform(
        post("/api/service/notice/customer")
            .content(objectMapper.writeValueAsString(1L))
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("서비스 완료후 이동경로 내역 저장")
  void endService() throws Exception {
    //given
    String authorization ="Bearer Token";
    MemberInfo info=MemberInfo.builder()
        .email("walkerservice@gmail.com")
        .role(Role.USER)
        .build();

    ServiceEndRequest request=ServiceEndRequest.builder()
        .reserveId(2L)
        .build();
    Map <String, Object> params = new HashMap <>();
    params.put("memberInfo", info);
    params.put("request", request);

    LocalDateTime expectedEndTime = LocalDateTime.of(2023, 12, 23, 2, 0,0,0);

    ServiceEndResponse response=ServiceEndResponse.builder()
        .endTime(expectedEndTime)
        .routeId(1L)
        .build();

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.isWalker(authorization)).willReturn(true);
    given(walkerService.saveServiceRoute(any(),any())).willReturn(response);

    //when
    ResultActions resultActions = mockMvc.perform(
        post("/api/service/finish")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(params))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.endTime").value(expectedEndTime.toString()))
        .andExpect(jsonPath("$.routeId").value(response.getRouteId().intValue()));
  }

}