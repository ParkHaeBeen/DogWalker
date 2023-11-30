package com.project.dogwalker.member.dto;

import com.project.dogwalker.domain.user.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@Builder
@ToString
public class MemberInfo {

  private final String email;
  private final Role role;


}
