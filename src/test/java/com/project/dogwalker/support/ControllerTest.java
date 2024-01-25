package com.project.dogwalker.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.member.controller.MemberController;
import com.project.dogwalker.member.service.OauthServiceImpl;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
    MemberController.class
})
@ActiveProfiles(profiles = "local")
public abstract class ControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @MockBean
  protected JwtTokenProvider jwtTokenProvider;

  @MockBean
  protected OauthServiceImpl oauthService;

  @MockBean
  protected RefreshTokenCookieProvider refreshTokenCookieProvider;

}
