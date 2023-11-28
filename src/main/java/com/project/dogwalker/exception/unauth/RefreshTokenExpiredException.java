package com.project.dogwalker.exception.unauth;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class RefreshTokenExpiredException extends CustomException {

  public RefreshTokenExpiredException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
