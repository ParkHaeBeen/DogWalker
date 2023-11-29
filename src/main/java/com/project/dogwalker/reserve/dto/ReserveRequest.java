package com.project.dogwalker.reserve.dto;

import java.time.LocalDateTime;
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
public class ReserveRequest {
  private Long walkerId;
  private LocalDateTime serviceDate;
  private Integer timeUnit;
  private Integer price;
  private String payMethod;
}
