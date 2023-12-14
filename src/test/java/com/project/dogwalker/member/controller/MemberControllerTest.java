package com.project.dogwalker.member.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.exception.member.LoginMemberNotFoundException;
import com.project.dogwalker.exception.unauth.RefreshTokenNotExistException;
import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.service.OauthServiceImpl;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(MemberController.class)
@Rollback
class MemberControllerTest {

  @MockBean
  private OauthServiceImpl oauthService;

  @MockBean
  private RefreshTokenCookieProvider refreshTokenCookieProvider;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

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
    ResultActions actions = mockMvc.perform(get("/api/login/" + type + "?code=" + code));

    //then
    actions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print());
  }

  @Test
  @DisplayName("새로운 회원이라 exception 발생")
  void loginFailOfNewMember() throws Exception {
    //given
    String code="code";
    String type="naver";


    given(oauthService.login(code,type)).willThrow(LoginMemberNotFoundException.class);

    //when
    ResultActions actions = mockMvc.perform(get("/api/login/" + type + "?code=" + code));

    //then
    actions.andExpect(status().isNotFound())
        .andDo(print());
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

    JoinUserRequest joinUserRequest=JoinUserRequest.builder().build();
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
            .multipart(HttpMethod.POST,"/api/join/user")
            .file(file)
            .file(new MockMultipartFile("joinRequest", "", "application/json", joinRequest.getBytes()))
            .accept(MediaType.APPLICATION_JSON)

    );

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print());
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

    JoinWalkerRequest request=JoinWalkerRequest.builder().build();


    given(oauthService.joinWalker(any(JoinWalkerRequest.class))).willReturn(loginResult);
    given(refreshTokenCookieProvider.generateCookie(loginResult.getRefreshToken())).willReturn(cookie);

    // when
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/join/walker")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));


    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print());
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
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/newtoken")
            .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie1));



    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print());
  }
  @Test
  @DisplayName("access token 만료시 accessToken,refreshToken 재지급-실패 : RefreshToken  존재하지 않아")
  void getNewToken_fail_notFoundRefreshToken() throws Exception {
    //given
    //when
    //then
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/newtoken")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(result ->
            Assertions.assertThat(result.getResolvedException()).isInstanceOf(
                RefreshTokenNotExistException.class)
        );
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
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refreshtoken")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(accessToken)));

    //then
    resultActions.andExpect(status().isOk())
        .andExpect(header().string("Set-cookie",containsString("RefreshToken="+refreshToken)))
        .andDo(print());
  }

}