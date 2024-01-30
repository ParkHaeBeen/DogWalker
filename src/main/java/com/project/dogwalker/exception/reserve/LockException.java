package com.project.dogwalker.exception.reserve;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class LockException extends CustomException {
  public LockException(final ErrorCode errorCode) {
    super(errorCode);
  }

}
