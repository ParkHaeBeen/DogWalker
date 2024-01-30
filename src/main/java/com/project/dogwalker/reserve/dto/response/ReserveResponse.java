package com.project.dogwalker.reserve.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReserveResponse {
  private LocalDateTime serviceDate;
  private String walkerName;
  private Integer timeUnit;
  private Integer price;
  private LocalDateTime payDate;
}
