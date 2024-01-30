package com.project.dogwalker.support.fixture;

import static com.project.dogwalker.domain.user.Role.USER;
import static com.project.dogwalker.domain.user.Role.WALKER;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.member.dto.MemberInfo;

public enum MemberInfoFixture {
  MEMBERINFO_WALKER("member1@gmail.com", WALKER),
  MEMBERINFO_USER("member2@gmail.com", USER)
  ;

  private final String userEmail;
  private final Role role;

  MemberInfoFixture(String userEmail , Role role) {
    this.userEmail = userEmail;
    this.role = role;
  }

  public MemberInfo 생성(){
    return MemberInfo.builder()
        .role(this.role)
        .email(this.userEmail)
        .build();
  }
}
