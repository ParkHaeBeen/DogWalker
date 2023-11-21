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
  private String googleClientId;
  @Value("${google.client.pw}")
  private String googleClientPw;
  @Value("${google.login.url}")
  private String googleApiUrl;
  @Value("${google.redirect.url}")
  private String redirectUrl;


  private final GoogleClient googleClient;

  @Override
  public String getLoginView(){
    String reqUrl = googleApiUrl+"client_id="
                          + googleClientId
                          + "&redirect_uri="+redirectUrl
                          +"&response_type=code&scope=email%20profile%20openid&access_type=offline";
    return reqUrl;
  }

  @Override
  public ClientResponse login(String code){
    GoogleResponse googleResponse = googleClient.getGoogleToken(GoogleRequest.builder()
                                                    .clientId(googleClientId)
                                                    .clientSecret(googleClientPw)
                                                    .code(code)
                                                    .redirectUri(redirectUrl)
                                                    .grantType("authorization_code")
                                                    .build());

    return googleClient.getGoogleDetailInfo(GoogleInfRequest.builder()
                                                    .idToken(googleResponse.getId_token())
                                                    .build());
  }

}
