package com.project.dogwalker.member.dto.join;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinWalkerSchedule {
  private String dayOfWeek;
  private Integer startTime;
  private Integer endTime;
}
