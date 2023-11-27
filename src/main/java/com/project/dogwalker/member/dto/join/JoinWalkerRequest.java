package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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
public class JoinWalkerRequest {

  @NotNull
  private JoinCommonRequest commonRequest;
  //예약 불가 날짜
  @Builder.Default
  private List<JoinWalkerSchedule> schedules=new ArrayList<>();

  //서비스 수행자마다 30,40,50분단위 가격
  @Builder.Default
  private List<JoinWalkerPrice> servicePrices=new ArrayList<>();
}
