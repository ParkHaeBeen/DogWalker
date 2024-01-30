package com.project.dogwalker.reserve.dto.request;

import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ReserveStatusRequest {

  @NotNull
  private Long reserveId;

  @NotNull
  private WalkerServiceStatus status;
}
