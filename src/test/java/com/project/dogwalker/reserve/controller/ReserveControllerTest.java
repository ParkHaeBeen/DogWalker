package com.project.dogwalker.reserve.controller;

import static com.project.dogwalker.domain.user.Role.USER;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.config.WebConfig;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.service.ReserveServiceImpl;
import java.time.LocalDateTime;
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

@WebMvcTest(ReserveController.class)
@Import(WebConfig.class)
class ReserveControllerTest {

  @MockBean
  private ReserveServiceImpl reserveService;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  @DisplayName("토큰 검증 실패로 실패")
  void reserve_check_fail() throws Exception {
    //given
    String authorization ="Bearer Token";
    given(jwtTokenProvider.validateToken(authorization)).willReturn(false);
    ReserveCheckRequest request=ReserveCheckRequest.builder().build();

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/reserve/check")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );

    //then
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("결제창으로 넘어가기전 해당 날짜 예약 여부 확인 - 성공")
  void reserve_check_success() throws Exception {
    //given
    String authorization ="Bearer Token";
    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    ReserveCheckRequest request=ReserveCheckRequest.builder().build();

    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/reserve/check")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    resultActions.andExpect(status().isOk());
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
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .timeUnit(30)
        .walkerId(1L)
        .build();
    MemberInfo memberInfo=MemberInfo.builder()
        .email(email)
        .role(role)
        .build();

    given(jwtTokenProvider.validateToken(authorization)).willReturn(true);
    given(jwtTokenProvider.getMemberInfo(authorization)).willReturn(memberInfo);

    //when
    ResultActions resultActions = mockMvc.perform(
        post("/api/reserve")
            .header(HttpHeaders.AUTHORIZATION , authorization)
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
    );


    //then
    verify(jwtTokenProvider, times(1)).getMemberInfo(authorization);

    resultActions.andExpect(status().isOk())
        .andDo(print());

    verify(reserveService).reserveService(memberInfo,request);
  }


}