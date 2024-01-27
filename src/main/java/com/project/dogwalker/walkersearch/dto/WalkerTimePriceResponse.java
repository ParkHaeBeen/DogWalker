package com.project.dogwalker.walkersearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class WalkerTimePriceResponse {

  private Integer timeUnit;
  private Integer serviceFee;
}
