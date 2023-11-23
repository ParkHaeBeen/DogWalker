package com.project.dogwalker.exception.member;

import static com.project.dogwalker.exception.ErrorCode.IMG_UPLOAD_FAIL;

import com.project.dogwalker.exception.CustomException;

public class ImgUploadFailException extends CustomException {

  public ImgUploadFailException( Exception exception) {
    super(IMG_UPLOAD_FAIL , exception);
  }
}
