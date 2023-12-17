package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinWalkerPrice {
  @NotNull
  private Integer timeUnit;
  @NotNull
  private Integer timeFee;
}
