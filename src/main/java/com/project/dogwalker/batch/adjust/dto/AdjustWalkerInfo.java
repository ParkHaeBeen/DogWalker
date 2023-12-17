package com.project.dogwalker.batch.adjust.dto;

import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.user.User;
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
public class AdjustWalkerInfo {
  private User walker;
  private PayHistory payHistory;
  private WalkerReserveServiceInfo reserveServiceInfo;
}
