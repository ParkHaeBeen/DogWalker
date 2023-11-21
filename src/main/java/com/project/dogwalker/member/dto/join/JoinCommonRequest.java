package com.project.dogwalker.member.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinCommonRequest {
  private String email;
  private String phoneNumber;
  private Double lat;
  private Double lnt;
  private String name;
}
