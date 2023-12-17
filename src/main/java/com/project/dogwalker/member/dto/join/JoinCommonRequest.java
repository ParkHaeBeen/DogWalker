package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JoinCommonRequest {
  @NotNull
  private String phoneNumber;
  @NotNull
  private Double lat;
  @NotNull
  private Double lnt;
  @NotNull
  private String name;

  @NotNull
  private String loginType;
  @NotNull
  private String accessToken;
}
