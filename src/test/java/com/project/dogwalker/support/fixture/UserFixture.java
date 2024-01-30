package com.project.dogwalker.support.fixture;

import static com.project.dogwalker.domain.user.Role.USER;
import static com.project.dogwalker.domain.user.Role.WALKER;

import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;

public enum UserFixture {
  USER_ONE(1L,"user1@gmail.com","010-1234-1234",12.0,
      15.0, USER,"user1"),
  USER_TWO(2L,"user2@gmail.com","010-2234-1234",12.0,
      17.0, USER,"user2"),
  WALKER_ONE(3L,"walker1@gmail.com","010-3234-1234",13.0,
      17.0, WALKER,"walker1"),
  WALKER_TWO(4L,"walker2@gmail.com","010-4234-1234",13.0,
      16.0, WALKER,"walker2");
  private final Long userId;
  private final String userEmail;
  private final String userPhoneName;
  private final Double userLat;
  private final Double userLnt;

  private final Role role;
  private final String userName;

  UserFixture(final Long userId ,final String userEmail ,final String userPhoneName ,final Double userLat ,
      final Double userLnt ,final Role role ,final String userName) {
    this.userId = userId;
    this.userEmail = userEmail;
    this.userPhoneName = userPhoneName;
    this.userLat = userLat;
    this.userLnt = userLnt;
    this.role = role;
    this.userName = userName;
  }

  public User 생성(){
    return User.builder()
        .userPhoneNumber(this.userPhoneName)
        .userId(this.userId)
        .userRole(this.role)
        .userLat(this.userLat)
        .userName(this.userName)
        .userLnt(this.userLnt)
        .userEmail(this.userEmail)
        .build();
  }

}
