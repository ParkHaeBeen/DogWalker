package com.project.dogwalker.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LoginResult {
  private String email;
  private String name;
  private String refreshToken;
  private String accessToken;
  private boolean newMember;
}
