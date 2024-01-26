package com.project.dogwalker.member.client;

import com.project.dogwalker.member.dto.ClientResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllOauths {

  private final Map<String,Oauth> oauthList;

  public String requestUrl(final String type){
    final Oauth oauth=getTypeOauth(type);
    return oauth.getLoginView();
  }

  public ClientResponse login(final String type, final String code){
    final Oauth oauth=getTypeOauth(type);
    return oauth.login(code);
  }

  public ClientResponse getUserInfo(final String type, final String accessToken){
    final Oauth oauth = getTypeOauth(type);
    return oauth.getUserInfo(accessToken);
  }


  /**
   * type 네이버, 구글에 따라서 Oauth 빈 가져오기
   * @param type
   */
  private Oauth getTypeOauth(final String type){
    return oauthList.get(type);
  }
}
