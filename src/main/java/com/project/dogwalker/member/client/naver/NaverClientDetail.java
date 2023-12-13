package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.common.config.FeignConfig;
import com.project.dogwalker.member.dto.naver.NaverClientResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naverdetail",url = "${naver.user.info.url}",configuration = FeignConfig.class)
public interface NaverClientDetail {
  @GetMapping("")
  NaverClientResponse getNaverDetailInfo(@RequestHeader Map<String,String> header);
}
