package com.project.dogwalker.member.dto.naver;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NaverRequest {


  private String grant_type;
  private String client_id;
  private String client_secret;
  private String code;
  private String state;
}
