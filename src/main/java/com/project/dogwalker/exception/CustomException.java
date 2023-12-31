package com.project.dogwalker.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final ErrorCode errorCode;
  private  String errorMessage;
  public CustomException(final ErrorCode errorCode,final Exception e) {
    super(e);
    this.errorCode = errorCode;
    this.errorMessage = e.getMessage();
  }

  public CustomException(final ErrorCode errorCode,final String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public CustomException(final ErrorCode errorCode){
    this.errorCode=errorCode;
    this.errorMessage= errorCode.getValue();
  }

}
