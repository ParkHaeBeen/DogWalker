package com.project.dogwalker.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.common.config.WebConfig;
import com.project.dogwalker.member.controller.MemberController;
import com.project.dogwalker.member.service.OauthServiceImpl;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
import com.project.dogwalker.walkersearch.controller.WalkerInfoController;
import com.project.dogwalker.walkersearch.service.WalkerInfoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
    MemberController.class,
    WalkerInfoController.class
})
@AutoConfigureRestDocs
@ActiveProfiles(profiles = "local")
@Import(WebConfig.class)
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

  @MockBean
  protected WalkerInfoServiceImpl walkerInfoService;

}
