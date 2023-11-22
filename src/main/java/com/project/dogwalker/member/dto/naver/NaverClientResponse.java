package com.project.dogwalker.member.dto.naver;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class NaverClientResponse {

  private String resultcode;
  private String message;
  private ResponseData response;

  @Getter
  @Setter
  public static class ResponseData{
    private String name;
    private String email;
  }
}
