package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinWalkerSchedule {
  @NotNull
  private String dayOfWeek;
  @NotNull
  private Integer startTime;
  @NotNull
  private Integer endTime;
}
