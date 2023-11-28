package com.project.dogwalker.exception.member;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class MemberNotFoundException extends CustomException {

  public MemberNotFoundException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
