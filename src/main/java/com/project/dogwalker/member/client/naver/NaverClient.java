package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.config.FeignConfig;
import com.project.dogwalker.member.dto.naver.NaverRequest;
import com.project.dogwalker.member.dto.naver.NaverResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "naver",url = "${naver.token.url}", configuration = FeignConfig.class)
public interface NaverClient {

  @GetMapping("")
  NaverResponse getNaverToken(@SpringQueryMap NaverRequest naverRequest);

}
