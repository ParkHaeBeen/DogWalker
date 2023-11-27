package com.project.dogwalker.exception.dto;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;

@Getter
public class TokenResponse {
  private String message;
  private ErrorCode errorCode;
  private String token;

  public TokenResponse(String message , ErrorCode errorCode , String token) {
    this.message = message;
    this.errorCode = errorCode;
    this.token = token;
  }

  public static TokenResponse from(final CustomException e,final String token){
    return new TokenResponse(e.getMessage(),e.getErrorCode(),token);
  }
}
