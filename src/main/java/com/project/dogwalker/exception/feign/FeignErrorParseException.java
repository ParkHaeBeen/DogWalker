package com.project.dogwalker.exception.feign;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;

public class FeignErrorParseException extends CustomException {

  public FeignErrorParseException(Exception e) {
    super(ErrorCode.FEIGN_PARSE_ERROR , e);
  }
}
