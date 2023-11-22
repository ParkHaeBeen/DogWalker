package com.project.dogwalker.exception.feign;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;

public class FeignCommonException extends CustomException {
  public FeignCommonException(String message) {
    super(ErrorCode.FEIGN_ERROR, message);
  }
}
