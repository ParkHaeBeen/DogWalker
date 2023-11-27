package com.project.dogwalker.member.client;

import com.project.dogwalker.member.dto.ClientResponse;

public interface Oauth {

  String getLoginView();
  ClientResponse login(String code);

  ClientResponse getUserInfo(String accessToken);
}
