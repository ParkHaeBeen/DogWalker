package com.project.dogwalker.exception.reserve;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveNotAvailableException extends CustomException {

  public ReserveNotAvailableException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
