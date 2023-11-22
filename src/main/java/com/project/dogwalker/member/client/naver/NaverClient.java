package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.member.dto.naver.NaverRequest;
import com.project.dogwalker.member.dto.naver.NaverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "naver",url = "${naver.token.url}")
public interface NaverClient {

  @GetMapping("/oauth2.0/token")
  NaverResponse getNaverToken(@SpringQueryMap NaverRequest naverRequest);

}
