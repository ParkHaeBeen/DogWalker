package com.project.dogwalker.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NOT_EXIST_ACCESS_TOKEN("토큰이 존재하지 않습니다"),
  IMG_UPLOAD_FAIL("이미지 업로드 실패하였습니다");
  private final String value;

  ErrorCode(final String value) {
    this.value = value;
  }
}
