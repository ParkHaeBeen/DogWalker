package com.project.dogwalker.walkerSearch.dto;

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
  private Double lnt;
  private Double lat;
  private int startPage;

  //default 10개
  @Builder.Default
  private int size=10;
}
