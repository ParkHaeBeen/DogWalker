package com.project.dogwalker.exception.member;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends CustomException {

  public MemberException(final ErrorCode errorCode) {
    super(errorCode);
  }
}