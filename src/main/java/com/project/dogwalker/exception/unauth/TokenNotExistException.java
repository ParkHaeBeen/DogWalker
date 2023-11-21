package com.project.dogwalker.exception.unauth;

import static com.project.dogwalker.exception.ErrorCode.*;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;

public class TokenNotExistException extends CustomException {

  public TokenNotExistException(Exception exception) {
    super(NOT_EXIST_ACCESS_TOKEN , exception);
  }
}
