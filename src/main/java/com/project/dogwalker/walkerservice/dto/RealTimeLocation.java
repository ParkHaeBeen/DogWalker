package com.project.dogwalker.walkerservice.dto;

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
public class RealTimeLocation {
  private Long reserveId;
  private Double lat;
  private Double lnt;
}
