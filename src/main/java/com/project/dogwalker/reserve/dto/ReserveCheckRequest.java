package com.project.dogwalker.reserve.dto;

import jakarta.validation.constraints.NotNull;
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
public class ReserveCheckRequest {
  @NotNull
  private Long walkerId;
  @NotNull
  private LocalDateTime serviceDate;
}
