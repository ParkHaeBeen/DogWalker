package com.project.dogwalker.walkerSearch.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WalkerPermUnAvailDate {
  @Column(name = "unavailable_day")
  private String dayOfWeak;
  @Column(name = "unavailable_time_start")
  private Integer startTime;
  @Column(name = "unavailable_time_end")
  private Integer endTime;

}
