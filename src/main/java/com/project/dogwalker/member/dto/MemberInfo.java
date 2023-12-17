package com.project.dogwalker.member.dto;

import com.project.dogwalker.domain.user.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@Builder
@ToString
public class MemberInfo {

  @NotNull
  private final String email;
  @NotNull
  private final Role role;


}
