package com.project.dogwalker.exception.reserve;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveRequestNotExistException extends CustomException {

  public ReserveRequestNotExistException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
