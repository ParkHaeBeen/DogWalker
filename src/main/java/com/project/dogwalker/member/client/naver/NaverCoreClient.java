package com.project.dogwalker.member.client.naver;

import com.project.dogwalker.member.dto.ClientResponse;
import com.project.dogwalker.member.dto.naver.NaverClientResponse;
import com.project.dogwalker.member.dto.naver.NaverRequest;
import com.project.dogwalker.member.dto.naver.NaverResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class NaverCoreClient {
  private final NaverClient naverClient;
  private final NaverClientDetail naverClientDetail;
  NaverResponse getNaverToken(NaverRequest request){
    return naverClient.getNaverToken(request);
  }

  ClientResponse getNaverDetailInfo(Map<String,String> header){
    NaverClientResponse naverDetailInfo = naverClientDetail.getNaverDetailInfo(header);

    log.info("naverDetailResponse ={}",naverDetailInfo);
    return ClientResponse.builder()
        .email(naverDetailInfo.getResponse().getEmail())
        .name(naverDetailInfo.getResponse().getName())
        .build();
  }
}
