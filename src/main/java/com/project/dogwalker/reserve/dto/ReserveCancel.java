package com.project.dogwalker.reserve.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ReserveCancel {
  @Getter
  @Builder
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class Request{
    @NotNull
    private Long reserveId;
    @Builder.Default
    private LocalDateTime now=LocalDateTime.now();
  }
  @Getter
  @Builder
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @EqualsAndHashCode
  public static class Response{
    @NotNull
    private LocalDateTime serviceDate;
    @NotNull
    private LocalDateTime cancelDate;
  }
}
