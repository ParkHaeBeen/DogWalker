package com.project.dogwalker.exception;

import static com.project.dogwalker.exception.ErrorCode.IMG_UPLOAD_FAIL;

public class ImgUploadFailException extends CustomException {

  public ImgUploadFailException(final Exception exception) {
    super(IMG_UPLOAD_FAIL , exception);
  }
}
