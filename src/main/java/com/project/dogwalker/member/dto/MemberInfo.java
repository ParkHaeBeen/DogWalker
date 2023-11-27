package com.project.dogwalker.member.dto;

import com.project.dogwalker.domain.user.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberInfo {

  private final String email;
  private final Role role;


}
