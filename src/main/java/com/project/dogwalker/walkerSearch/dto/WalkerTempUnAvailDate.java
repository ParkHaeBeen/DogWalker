package com.project.dogwalker.walkerSearch.dto;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
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
public class WalkerTempUnAvailDate {
  @Column(name = "unavailable_date")
  private LocalDateTime unAvailDate;

}
