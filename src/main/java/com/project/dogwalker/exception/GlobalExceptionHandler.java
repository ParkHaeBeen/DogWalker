package com.project.dogwalker.exception;

import com.project.dogwalker.exception.batch.ReserveBatchException;
import com.project.dogwalker.exception.dto.ExceptionResponse;
import com.project.dogwalker.exception.dto.TokenResponse;
import com.project.dogwalker.exception.feign.FeignBadRequestException;
import com.project.dogwalker.exception.feign.FeignCommonException;
import com.project.dogwalker.exception.feign.FeignErrorParseException;
import com.project.dogwalker.exception.feign.FeignNotFoundException;
import com.project.dogwalker.exception.feign.FeignServerException;
import com.project.dogwalker.exception.member.AuthMemberException;
import com.project.dogwalker.exception.member.MemberException;
import com.project.dogwalker.exception.notice.NoticeException;
import com.project.dogwalker.exception.notice.SseException;
import com.project.dogwalker.exception.reserve.LockException;
import com.project.dogwalker.exception.reserve.ReserveException;
import com.project.dogwalker.exception.unauth.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String LOG_ERROR_MESSAGE = "Class : {}, Code : {}, Message : {}";

  @ExceptionHandler(ImgUploadFailException.class)
  public ResponseEntity<ExceptionResponse> handleException(final ImgUploadFailException e) {
    log.info(LOG_ERROR_MESSAGE , e.getClass() , e.getErrorCode() , e.getErrorCode().getValue());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignBadRequestException.class)
  public ResponseEntity<ExceptionResponse> handleException(final FeignBadRequestException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorCode().getValue());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignCommonException.class)
  public ResponseEntity<ExceptionResponse> handleException(final FeignCommonException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignErrorParseException.class)
  public ResponseEntity<ExceptionResponse> handleException(final FeignErrorParseException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleException(final FeignNotFoundException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignServerException.class)
  public ResponseEntity<ExceptionResponse> handleFeignServer(final FeignServerException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.internalServerError().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(AuthMemberException.class)
  public ResponseEntity<TokenResponse> handleException(final AuthMemberException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TokenResponse.from(e,e.getToken()));
  }

  @ExceptionHandler(MemberException.class)
  public ResponseEntity<ExceptionResponse> handleException(final MemberException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }
  @ExceptionHandler(ReserveException.class)
  public ResponseEntity<ExceptionResponse> handleException(final ReserveException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(LockException.class)
  public ResponseEntity<ExceptionResponse> handleException(final LockException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(ReserveBatchException.class)
  public ResponseEntity<ExceptionResponse> handleException(final ReserveBatchException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(TokenException.class)
  public ResponseEntity<ExceptionResponse> handleException(final TokenException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(NoticeException.class)
  public ResponseEntity<ExceptionResponse> handleException(final NoticeException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(SseException.class)
  public ResponseEntity<ExceptionResponse> handleException(final SseException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleException(final MethodArgumentNotValidException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getStatusCode(),e.getMessage());
    return ResponseEntity.status(e.getStatusCode()).body(e);
  }
}

