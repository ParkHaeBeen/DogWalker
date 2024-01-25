package com.project.dogwalker.exception.member;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AuthMemberException extends CustomException {
  private String token;
  public AuthMemberException(final ErrorCode errorCode ,final String token) {
    super(errorCode);
    this.token = token;
  }
}
