package com.project.dogwalker.reserve.dto;

import jakarta.validation.constraints.NotNull;
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
  @NotNull
  private Long walkerId;
  @NotNull
  private LocalDateTime serviceDateTime;
  @NotNull
  private Integer timeUnit;
  @NotNull
  private Integer price;
  @NotNull
  private String payMethod;
}
