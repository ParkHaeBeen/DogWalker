package com.project.dogwalker.member.dto.naver;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverResponse {

  private String access_token;
  private String refresh_token;
  private String token_type;
  private String expires_in;
}
