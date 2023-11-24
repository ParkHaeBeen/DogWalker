package com.project.dogwalker.exception.feign;

import static com.project.dogwalker.exception.ErrorCode.FEIGN_NOT_FOUND;

import com.project.dogwalker.exception.CustomException;

public class FeignNotFoundException extends CustomException {

  public FeignNotFoundException(String message) {
    super(FEIGN_NOT_FOUND, message);
  }
}
