package com.project.dogwalker.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
  private String token;
  private String email;
  private String name;

  public static LoginResponse from(final LoginResult result){
    return LoginResponse.builder()
        .email(result.getEmail())
        .name(result.getName())
        .token(result.getAccessToken())
        .build();
  }

}
