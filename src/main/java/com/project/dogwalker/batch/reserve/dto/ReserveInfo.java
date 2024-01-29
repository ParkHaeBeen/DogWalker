package com.project.dogwalker.batch.reserve.dto;

import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReserveInfo {
  private PayHistory payHistory;
  private WalkerReserveServiceInfo reserveServiceInfo;
}
