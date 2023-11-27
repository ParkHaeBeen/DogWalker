package com.project.dogwalker.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
  NOT_EXIST_ACCESS_TOKEN("토큰이 존재하지 않습니다"),
  IMG_UPLOAD_FAIL("이미지 업로드 실패하였습니다"),
  FEIGN_SERVER_API_ERROR("api 에러 발생하였습니다"),
  FEIGN_NOT_FOUND("해당 리소스를 찾지 못했습니다"),
  FEIGN_BAD_REQUEST("잘못된 요청입니다"),
  FEIGN_PARSE_ERROR("파싱 에러가 발생했습니다"),
  FEIGN_ERROR("feign 예외 발생했습니다"),
  NOT_WRITE_SERVICE_PRICE("서비수 수행자는 회원가입시 서비스 수행에 대한 가격을 기입해야합니다"),
  NOT_EXIST_MEMBER("멤버가 존재하지 않습니다. 회원가입해주세요");
  private final String value;

  ErrorCode(final String value) {
    this.value = value;
  }
}
