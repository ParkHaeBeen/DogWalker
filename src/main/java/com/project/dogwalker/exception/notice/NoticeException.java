package com.project.dogwalker.exception.notice;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NoticeException extends CustomException {

  public NoticeException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
