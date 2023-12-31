package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.member.client.Oauth;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.naver.NaverRequest;
import com.project.dogwalker.member.dto.naver.NaverResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("naver")
@RequiredArgsConstructor
@Slf4j
public class NaverOauth implements Oauth {

  @Value("${naver.client.id}")
  private String NAVER_CLIENT_ID;
  @Value("${naver.client.pw}")
  private String NAVER_CLIENT_PW;
  @Value("${naver.login.url}")
  private String NAVER_API_URL;
  @Value("${naver.redirect.url}")
  private String NAVER_REDIRECT_URL;

  private final NaverCoreClient naverCoreClient;
  @Override
  public String getLoginView() {
    String reqUrl=NAVER_API_URL+"response_type=code&"
        +"client_id="+NAVER_CLIENT_ID+"&state=STATE_STRING&redirect_uri="
        +NAVER_REDIRECT_URL;
    return reqUrl;
  }

  @Override
  public ClientResponse login(final String code) {
    log.info("token in oauth = {}",code);
    NaverResponse naverResponse = naverCoreClient.getNaverToken(NaverRequest.builder()
                                                .grant_type("authorization_code")
                                                .client_id(NAVER_CLIENT_ID)
                                                .client_secret(NAVER_CLIENT_PW)
                                                .code(code)
                                                .state("9kgsGTfH4j7IyAkg")
                                                .build());
    log.info("response ={}",naverResponse);

    Map<String,String> headerMap=new HashMap<>();
    headerMap.put("authorization","Bearer "+naverResponse.getAccessToken());

    ClientResponse naverDetailInfo = naverCoreClient.getNaverDetailInfo(headerMap);
    naverDetailInfo.setIdToken(naverResponse.getAccessToken());
    return naverDetailInfo;
  }

  @Override
  public ClientResponse getUserInfo(final String accessToken){
    Map<String,String> headerMap=new HashMap<>();
    headerMap.put("authorization","Bearer "+accessToken);

    final ClientResponse naverDetailInfo = naverCoreClient.getNaverDetailInfo(headerMap);
    return naverDetailInfo;
  }
}
