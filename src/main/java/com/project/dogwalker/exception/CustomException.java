package com.project.dogwalker.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final ErrorCode errorCode;
  private final String errorMessage;
  public CustomException(final ErrorCode errorCode,Exception e) {
    this.errorCode=errorCode;
    this.errorMessage=e.getMessage();
  }
}
