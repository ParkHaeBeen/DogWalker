package com.project.dogwalker.exception.notice;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class NoticeNotFoundException extends CustomException {

  public NoticeNotFoundException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
