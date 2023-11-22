package com.project.dogwalker.member.client;

import com.project.dogwalker.member.dto.ClientResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AllOauths {

  private final Map<String,Oauth> oauthList;

  public String requestUrl(String type){
    Oauth oauth=getOauth(type);
    return oauth.getLoginView();
  }

  public ClientResponse login(String type,String code){
    Oauth oauth=getOauth(type);
    return oauth.login(code);
  }

  public Oauth getOauth(String type){
    return oauthList.get(type);
  }
}
