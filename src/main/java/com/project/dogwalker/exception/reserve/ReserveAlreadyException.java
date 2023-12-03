package com.project.dogwalker.exception.reserve;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveAlreadyException extends CustomException {

  public ReserveAlreadyException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
