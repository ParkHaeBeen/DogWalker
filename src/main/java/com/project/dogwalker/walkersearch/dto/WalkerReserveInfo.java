package com.project.dogwalker.walkersearch.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class WalkerReserveInfo {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request{
    @NotNull
    private Long walkerId;
    @NotNull
    private LocalDate checkReserveDate;
  }
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @ToString
  public static class Response{
    @NotNull
    private LocalDateTime reserveDate;
  }
}
