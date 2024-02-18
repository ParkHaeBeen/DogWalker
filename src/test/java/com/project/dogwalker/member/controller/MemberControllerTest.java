package com.project.dogwalker.member.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.dogwalker.exception.member.AuthMemberException;
import com.project.dogwalker.exception.unauth.TokenException;
import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinCommonRequest;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerPrice;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerSchedule;
import com.project.dogwalker.support.ControllerTest;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class MemberControllerTest extends ControllerTest {

  @Test
  @DisplayName("회원 db에 존재하여 로그인 성공")
  void loginSuccess() throws Exception {
    //given
    String code="code";
    String type="naver";
    String name="hello";
    String email="member1@hello.com";
    String accessToken="accessToken";
    String refreshToken="refreshToken";

    LoginResult result=LoginResult.builder()
        .email(email)
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .name(name)
        .build();

    ResponseCookie cookie=ResponseCookie.from("RefreshToken",refreshToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .sameSite(SameSite.NONE.attributeValue())
                                .maxAge(Duration.ofMillis(604800))
                                .build();

    given(oauthService.login(code,type)).willReturn(result);
    given(refreshTokenCookieProvider.generateCookie(refreshToken)).willReturn(cookie);

    //when
    ResultActions actions = mockMvc.perform(get("/members/login/" + type + "?code=" + code));

    //then
    actions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print())
        .andDo(document("member/login",
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint())
    ));
  }

  @Test
  @DisplayName("새로운 회원이라 exception 발생")
  void loginFailOfNewMember() throws Exception {
    //given
    String code="code";
    String type="naver";


    given(oauthService.login(code,type)).willThrow(AuthMemberException.class);

    //when
    ResultActions actions = mockMvc.perform(get("/members/login/" + type + "?code=" + code));

    //then
    actions.andExpect(status().isNotFound())
        .andDo(print())
        .andDo(document("member/login/exception",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("고객으로 회원가입")
  void newCustomer() throws Exception {
    //given
    String name="hello";
    String email="member2@hello.com";
    String accessToken="accessToken";
    String refreshToken="refreshToken";
    LoginResult result=LoginResult.builder()
        .name(name)
        .refreshToken(refreshToken)
        .email(email)
        .accessToken(accessToken)
        .build();
    JoinCommonRequest commonRequest = JoinCommonRequest.builder()
        .loginType("naver")
        .accessToken("naver token")
        .name("푸들 주인")
        .lat(105.0)
        .lnt(12.0)
        .phoneNumber("010-4568-4568")
        .build();

    JoinUserRequest joinUserRequest=JoinUserRequest.builder()
        .commonRequest(commonRequest)
        .dogBirth(LocalDateTime.of(2022,01,01,0,0))
        .dogType("푸들")
        .dogName("달빈")
        .dogDescription("귀여웡")
        .build();
    String joinRequest=objectMapper.writeValueAsString(joinUserRequest);

    MockMultipartFile file = new MockMultipartFile("dogImg", "dog-image.jpg", "image/jpeg", "dog image content".getBytes());
    ResponseCookie cookie=ResponseCookie.from("RefreshToken",refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite(SameSite.NONE.attributeValue())
        .maxAge(Duration.ofMillis(604800))
        .build();

    given(oauthService.joinCustomer(any(),any())).willReturn(result);
    given(refreshTokenCookieProvider.generateCookie(refreshToken)).willReturn(cookie);

    //when
    ResultActions resultActions = mockMvc.perform(
        MockMvcRequestBuilders
            .multipart(HttpMethod.POST,"/members/user")
            .file(file)
            .file(new MockMultipartFile("joinRequest", "", "application/json", joinRequest.getBytes()))
            .accept(MediaType.APPLICATION_JSON)

    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print())
        .andDo(document("member/join/user",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("서비스 수행자로 회원가입")
  void newServiceWalker() throws Exception {
    //given
    String name="hello";
    String email="member3@hello.com";
    String accessToken="accessToken";
    String refreshToken="refreshToken";
    LoginResult loginResult=LoginResult.builder()
        .name(name)
        .refreshToken(refreshToken)
        .email(email)
        .accessToken(accessToken)
        .build();

    ResponseCookie cookie=ResponseCookie.from("RefreshToken",refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite(SameSite.NONE.attributeValue())
        .maxAge(Duration.ofMillis(604800))
        .build();

    JoinCommonRequest commonRequest = JoinCommonRequest.builder()
        .loginType("naver")
        .accessToken("naver token")
        .name("푸들 주인")
        .lat(105.0)
        .lnt(12.0)
        .phoneNumber("010-4568-4568")
        .build();

    JoinWalkerSchedule joinWalkerRequest1 = JoinWalkerSchedule.builder()
        .dayOfWeek("MON")
        .startTime(10)
        .endTime(17)
        .build();

    JoinWalkerSchedule joinWalkerRequest2 = JoinWalkerSchedule.builder()
        .dayOfWeek("TUE")
        .startTime(10)
        .endTime(17)
        .build();

    JoinWalkerPrice joinWalkerPrice1 = JoinWalkerPrice.builder()
        .timeFee(10000)
        .timeUnit(30)
        .build();

    JoinWalkerPrice joinWalkerPrice2 = JoinWalkerPrice.builder()
        .timeFee(12000)
        .timeUnit(40)
        .build();
    JoinWalkerRequest request=JoinWalkerRequest.builder()
        .commonRequest(commonRequest)
        .schedules(List.of(joinWalkerRequest1,joinWalkerRequest2))
        .servicePrices(List.of(joinWalkerPrice1,joinWalkerPrice2))
        .build();


    given(oauthService.joinWalker(any(JoinWalkerRequest.class))).willReturn(loginResult);
    given(refreshTokenCookieProvider.generateCookie(loginResult.getRefreshToken())).willReturn(cookie);

    // when
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/walker")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));


    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print())
        .andDo(document("member/join/walker",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급-성공")
  void getNewToken_success() throws Exception {
    //given
    String accessToken="accessToken";
    String refreshToken="refreshToken";
    IssueToken issueToken=IssueToken.builder()
        .refreshToken(refreshToken)
        .accessToken(accessToken)
        .build();
    ResponseCookie cookie=ResponseCookie.from("RefreshToken",refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite(SameSite.NONE.attributeValue())
        .maxAge(Duration.ofMillis(604800))
        .build();

    given(oauthService.generateToken(refreshToken)).willReturn(issueToken);
    given(refreshTokenCookieProvider.generateCookie(refreshToken)).willReturn(cookie);

    Cookie cookie1=new Cookie("RefreshToken",refreshToken);
    //when
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/auth/newtoken")
            .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie1));



    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print())
        .andDo(document("member/accesstoken",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }
  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급-실패 : RefreshToken  존재하지 않아")
  void getNewToken_fail_notFoundRefreshToken() throws Exception {
    //given
    //when
    //then
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/auth/newtoken")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(result ->
            Assertions.assertThat(result.getResolvedException()).isInstanceOf(
                TokenException.class)
        )
        .andDo(document("member/accesstoken/fail",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

  @Test
  @DisplayName("refreshToken 만료시 프론트단에서 재발급 요청 - 성공")
  void reIssueRefreshToken_success() throws Exception {
    //given
    String accessToken="accessToken";
    String refreshToken="refreshToken";
    ResponseCookie cookie=ResponseCookie.from("RefreshToken",refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite(SameSite.NONE.attributeValue())
        .maxAge(Duration.ofMillis(604800))
        .build();

    given(oauthService.generateNewRefreshToken(anyString())).willReturn(refreshToken);
    given(refreshTokenCookieProvider.generateCookie(refreshToken)).willReturn(cookie);

    //when
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/members/auth/refreshtoken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(accessToken)));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print())
        .andDo(document("member/refreshtoken",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint())));
  }

}