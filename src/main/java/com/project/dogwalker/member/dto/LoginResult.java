package com.project.dogwalker.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LoginResult {
  private String email;
  private String name;
  private String refreshToken;
  private String accessToken;
}
