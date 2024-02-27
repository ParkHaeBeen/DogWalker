package com.project.dogwalker.exception.batch;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class AdjustBatchException extends CustomException {

  public AdjustBatchException(final ErrorCode errorCode,final Exception e) {
    super(errorCode,e);
  }

  public AdjustBatchException(final ErrorCode errorCode) {
    super(errorCode);
  }
}
