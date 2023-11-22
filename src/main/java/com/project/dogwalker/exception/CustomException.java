package com.project.dogwalker.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final ErrorCode errorCode;
  private final String errorMessage;
  public CustomException(ErrorCode errorCode, Exception e) {
    super(e);
    this.errorCode = errorCode;
    this.errorMessage = e.getMessage();
  }

  public CustomException(ErrorCode errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }
}
