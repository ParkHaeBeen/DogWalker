package com.project.dogwalker.walkerservice.dto;

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
public class ServiceCheckRequest {
  @NotNull
  private LocalDateTime nowDate;
  @NotNull
  private Long reserveId;
}
