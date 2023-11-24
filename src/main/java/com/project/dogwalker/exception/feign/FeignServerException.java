package com.project.dogwalker.exception.feign;

import static com.project.dogwalker.exception.ErrorCode.FEIGN_SERVER_API_ERROR;

import com.project.dogwalker.exception.CustomException;

public class FeignServerException extends CustomException {

  public FeignServerException(String message) {
    super(FEIGN_SERVER_API_ERROR,message);
  }
}
