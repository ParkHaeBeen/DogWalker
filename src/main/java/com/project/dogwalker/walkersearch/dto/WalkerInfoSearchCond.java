package com.project.dogwalker.walkersearch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalkerInfoSearchCond {

  @Builder.Default
  private String walkerName="";
  @NotNull
  private Double lnt;
  @NotNull
  private Double lat;
}
