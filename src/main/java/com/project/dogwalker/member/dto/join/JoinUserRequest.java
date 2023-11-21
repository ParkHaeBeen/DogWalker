package com.project.dogwalker.member.dto.join;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinUserRequest {

  private JoinCommonRequest commonRequest;

  //강아지 정보
  private LocalDateTime dogBirth;
  private String dotName;
  private String dotType;
  private String dogDescription;
}
