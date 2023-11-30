package com.project.dogwalker.reserve.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ReserveRequest {
  private Long walkerId;
  private LocalDateTime serviceDate;
  private Integer timeUnit;
  private Integer price;
  private String payMethod;
}
