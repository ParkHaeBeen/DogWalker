package com.project.dogwalker.member.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaverClientResponse {

  private String resultcode;
  private String message;
  private ResponseData response;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class ResponseData{
    private String id;
    private String name;
    private String email;
  }
}
