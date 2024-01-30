package com.project.dogwalker.reserve.controller;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_ACCEPT;
import static com.project.dogwalker.domain.user.Role.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.response.ReserveListResponse;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import com.project.dogwalker.reserve.dto.response.ReserveResponse;
import com.project.dogwalker.reserve.dto.request.ReserveStatusRequest;
import com.project.dogwalker.support.ControllerTest;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ReserveControllerTest extends ControllerTest {

  @Test
  @DisplayName("토큰 검증 실패로 실패")
  void reserve_check_fail() throws Exception {
    //given
    String authorization ="Bearer Token";
    given(jwtTokenProvider.validateToken(authorization)).willReturn(false);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/reserve/check")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .queryParam("walkerId",String.valueOf(1L))
            .queryParam("serviceDate",LocalDateTime.now().toString())
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions
        .andExpect(status().isUnauthorized())
        .andDo(document("reserve/check/fail",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("결제창으로 넘어가기전 해당 날짜 예약 여부 확인 - 성공")
  void reserve_check_success() throws Exception {
    //given
    String authorization ="Bearer Token";
    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/reserve/check")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .queryParam("walkerId",String.valueOf(1L))
            .queryParam("serviceDate",LocalDateTime.now().toString())
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("reserve/check/success",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("예약 및 결제 진행 성공")
  void reserve_success() throws Exception {
    //given
    String email="dal@gmail.com";
    Role role= USER;
    String authorization ="Bearer Token";
    ReserveRequest request=ReserveRequest.builder()
        .payMethod("card")
        .price(1000)
        .serviceDateTime(LocalDateTime.of(2023,12,12,12,30))
        .timeUnit(30)
        .walkerId(1L)
        .build();
    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(role)
        .build();

    ReserveResponse reserveResponse = ReserveResponse.builder()
        .payDate(LocalDateTime.now())
        .walkerName("walker")
        .price(1000)
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .timeUnit(30)
        .build();

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(authorization)).willReturn(memberInfo);
    given(reserveService.reserveService(any(),any())).willReturn(reserveResponse);

    //when
    ResultActions resultActions = mockMvc.perform(
        post("/reserve")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    verify(jwtTokenProvider, times(1)).getMemberInfo(authorization);

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("reserve/request",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

    verify(reserveService).reserveService(memberInfo,request);
  }

  @Test
  @DisplayName("서비스 수행자 예약 요청 수락/ 거부")
  void changeRequestServiceStatus_success() throws Exception{
    //given
    String authorization="Bearer token";
    MemberInfo memberInfo=MemberInfo.builder()
        .email("request@gmail.com")
        .role(Role.WALKER)
        .build();

    ReserveStatusRequest request=ReserveStatusRequest.builder()
        .status(WALKER_ACCEPT)
        .reserveId(1L)
        .build();


    given(jwtTokenProvider.validateToken(anyString())).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(anyString())).willReturn(memberInfo);
    given(jwtTokenProvider.isWalker(anyString())).willReturn(true);

    //when
    ResultActions resultActions = mockMvc.perform(
        patch("/reserve" )
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("reserve/acceptRefuse",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));

  }

  @Test
  @DisplayName("예약 하루전까지 취소 가능")
  void reserveCancel_success() throws Exception {
    //given
    String email="dal@gmail.com";
    Role role= USER;
    String authorization ="Bearer Token";
    ReserveCancel.Request request=ReserveCancel.Request.builder()
        .reserveId(1L)
        .build();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(role)
        .build();

    ReserveCancel.Response response = ReserveCancel.Response.builder()
        .cancelDate(LocalDateTime.now())
        .serviceDate(LocalDateTime.of(2024,03,12,12,30))
        .build();

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(authorization)).willReturn(memberInfo);
    given(reserveService.reserveCancel(any(),any())).willReturn(response);

    //when
    ResultActions resultActions = mockMvc.perform(
        delete("/reserve")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    verify(jwtTokenProvider, times(1)).getMemberInfo(authorization);

    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("reserve/cancel",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));;

    verify(reserveService).reserveCancel(memberInfo,request);
  }

  @Test
  @DisplayName("예약 리스트 조회")
  void reserveList() throws Exception {
    //given
    String email="dal@gmail.com";
    Role role= USER;
    String authorization ="Bearer Token";

    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(role)
        .build();

    ReserveListResponse response1=ReserveListResponse.builder()
        .reserveId(1L)
        .serviceStatus(WALKER_ACCEPT)
        .price(10000)
        .timeUnit(50)
        .role(role)
        .serviceDate(LocalDateTime.now())
        .build();

    ReserveListResponse response2=ReserveListResponse.builder()
        .reserveId(2L)
        .serviceStatus(WALKER_ACCEPT)
        .price(10000)
        .timeUnit(50)
        .role(role)
        .serviceDate(LocalDateTime.now().plusDays(10))
        .build();
    List<ReserveListResponse> responses=List.of(response2,response1);

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(authorization)).willReturn(memberInfo);
    given(reserveService.getReserveList(any(),any())).willReturn(responses);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/reserve")
            .param("page","0")
            .param("size","10")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("reserve/list",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));;

    verify(reserveService).getReserveList(any(),any());
  }

  @Test
  @DisplayName("예약 상세 조회")
  void reserveDetail() throws Exception {
    //given
    String email="dal@gmail.com";
    Role role= USER;
    String authorization ="Bearer Token";

    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(role)
        .build();
    ReserveResponse reserveResponse = ReserveResponse.builder()
        .serviceDate(LocalDateTime.now().plusDays(10))
        .payDate(LocalDateTime.now().minusDays(1))
        .price(10000)
        .timeUnit(30)
        .walkerName("hello")
        .build();

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(authorization)).willReturn(memberInfo);
    given(reserveService.getReserveDetail(any(),anyLong())).willReturn(reserveResponse);

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/reserve/{reserveId}",1L)
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    resultActions.andExpect(status().isOk())
        .andDo(print())
        .andDo(document("reserve/detail",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));;

    verify(reserveService).getReserveDetail(any(),anyLong());
  }

}