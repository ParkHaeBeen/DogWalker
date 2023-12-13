package com.project.dogwalker.member.client.goolge;

import com.project.dogwalker.common.config.FeignConfig;
import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.google.GoogleInfRequest;
import com.project.dogwalker.member.dto.google.GoogleRequest;
import com.project.dogwalker.member.dto.google.GoogleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "google",url = "${google.auth.url}",configuration = FeignConfig.class)
public interface GoogleClient {

  @PostMapping("/token")
  GoogleResponse getGoogleToken(GoogleRequest googleRequest);

  @PostMapping("/tokeninfo")
  ClientResponse getGoogleDetailInfo(GoogleInfRequest googleInfRequest);
}
