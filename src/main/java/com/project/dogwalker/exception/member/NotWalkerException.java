package com.project.dogwalker.exception.member;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NotWalkerException extends CustomException {

  public NotWalkerException(final ErrorCode errorCode ) {
    super(errorCode);
  }
}
