package com.project.dogwalker.exception.member;

import static com.project.dogwalker.exception.ErrorCode.*;

import com.project.dogwalker.exception.CustomException;
import com.project.dogwalker.exception.ErrorCode;

public class ImgUploadFailException extends CustomException {

  public ImgUploadFailException( Exception exception) {
    super(IMG_UPLOAD_FAIL , exception);
  }
}
