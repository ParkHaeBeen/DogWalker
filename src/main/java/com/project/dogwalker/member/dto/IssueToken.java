package com.project.dogwalker.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueToken {

  private String accessToken;
  private String refreshToken;


}
