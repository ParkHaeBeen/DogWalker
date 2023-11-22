package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinWalkerRequest {

  @NotNull
  private JoinCommonRequest commonRequest;
  //예약 불가 날짜
  @Builder.Default
  private List<JoinWalkerSchedule> schedules=new ArrayList<>();
}
