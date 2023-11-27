package com.project.dogwalker.member.dto.join;

import jakarta.validation.constraints.NotNull;
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
public class JoinUserRequest {

  @NotNull
  private JoinCommonRequest commonRequest;

  //강아지 정보
  @NotNull
  private LocalDateTime dogBirth;
  @NotNull
  private String dogName;
  @NotNull
  private String dogType;
  @NotNull
  private String dogDescription;




}
