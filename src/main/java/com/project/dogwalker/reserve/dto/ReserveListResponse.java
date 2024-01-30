package com.project.dogwalker.reserve.dto;

import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
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
public class ReserveListResponse {
  private Long reserveId;
  private LocalDateTime serviceDate;
  private Integer timeUnit;
  private Integer price;
  private Role role;
  private WalkerServiceStatus serviceStatus;
}
