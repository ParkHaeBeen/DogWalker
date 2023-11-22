package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.member.dto.ClientResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naver2",url = "${naver.user.info.url}")
public interface NaverClient2 {
  @GetMapping("")
  ClientResponse getNaverDetailInfo(@RequestHeader Map<String,String> header);
}
