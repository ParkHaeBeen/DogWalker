package com.project.dogwalker.exception.notice;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class SseException extends CustomException {

  public SseException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
