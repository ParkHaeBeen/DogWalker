package com.project.dogwalker.member.client.goolge;

import com.project.dogwalker.member.client.Oauth;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.google.GoogleInfRequest;
import com.project.dogwalker.member.dto.google.GoogleRequest;
import com.project.dogwalker.member.dto.google.GoogleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("google")
@RequiredArgsConstructor
@Slf4j
public class GoogleOauth implements Oauth {

  @Value("${google.client.id}")
  private String GOOGLE_CLIENT_ID;
  @Value("${google.client.pw}")
  private String GOOGLE_CLIENT_PW;
  @Value("${google.login.url}")
  private String GOOGLE_API_URL;
  @Value("${google.redirect.url}")
  private String GOOGLE_REDIRECT_URL;


  private final GoogleClient googleClient;

  @Override
  public String getLoginView(){
    String reqUrl = GOOGLE_API_URL+"client_id="
                          + GOOGLE_CLIENT_ID
                          + "&redirect_uri="+GOOGLE_REDIRECT_URL
                          +"&response_type=code&scope=email%20profile%20openid&access_type=offline";
    return reqUrl;
  }

  /**
   * 토큰 받아와서 유저 정보 받아오기
   * @param code
   */
  @Override
  public ClientResponse login(final String code){
    final GoogleResponse googleResponse = googleClient.getGoogleToken(GoogleRequest.builder()
                                                    .clientId(GOOGLE_CLIENT_ID)
                                                    .clientSecret(GOOGLE_CLIENT_PW)
                                                    .code(code)
                                                    .redirectUri(GOOGLE_REDIRECT_URL)
                                                    .grantType("authorization_code")
                                                    .build());

    ClientResponse googleDetailInfo = googleClient.getGoogleDetailInfo(GoogleInfRequest.builder()
        .idToken(googleResponse.getIdToken())
        .build());
    googleDetailInfo.setIdToken(googleResponse.getIdToken());
    return googleDetailInfo;
  }

}
