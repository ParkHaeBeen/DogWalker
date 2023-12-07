package com.project.dogwalker.exception.batch;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ReserveBatchException extends CustomException {

  public ReserveBatchException(ErrorCode errorCode ,
      Exception e) {
    super(errorCode , e);
  }
}
