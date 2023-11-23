package com.project.dogwalker.exception.member;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;
import lombok.Getter;


@Getter
public class WalkerNotWritePriceException extends CustomException {

  public WalkerNotWritePriceException(final ErrorCode errorCode ,final String message) {
    super(errorCode , message);
  }

  public WalkerNotWritePriceException(final ErrorCode errorCode){
    super(errorCode);
  }
}
