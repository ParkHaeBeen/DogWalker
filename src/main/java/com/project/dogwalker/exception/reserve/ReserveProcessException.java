package com.project.dogwalker.exception.reserve;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveProcessException extends CustomException {

  public ReserveProcessException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
