package com.project.dogwalker.walkerservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class RealTimeLocationRequest {
  @NotNull
  private Long reserveId;
  @NotNull
  private Double lat;
  @NotNull
  private Double lnt;
}
