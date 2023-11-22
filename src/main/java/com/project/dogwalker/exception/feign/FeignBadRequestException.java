package com.project.dogwalker.exception.feign;

import static com.project.dogwalker.exception.ErrorCode.FEIGN_BAD_REQUEST;

import com.project.dogwalker.exception.CustomException;

public class FeignBadRequestException extends CustomException {

  public FeignBadRequestException(String exception) {
    super(FEIGN_BAD_REQUEST , exception);
  }
}
