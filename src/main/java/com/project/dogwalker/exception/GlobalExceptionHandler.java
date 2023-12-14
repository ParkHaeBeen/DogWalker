package com.project.dogwalker.exception;

import com.project.dogwalker.exception.dto.ExceptionResponse;
import com.project.dogwalker.exception.dto.TokenResponse;
import com.project.dogwalker.exception.feign.FeignBadRequestException;
import com.project.dogwalker.exception.feign.FeignCommonException;
import com.project.dogwalker.exception.feign.FeignErrorParseException;
import com.project.dogwalker.exception.feign.FeignNotFoundException;
import com.project.dogwalker.exception.feign.FeignServerException;
import com.project.dogwalker.exception.member.ImgUploadFailException;
import com.project.dogwalker.exception.member.LoginMemberNotFoundException;
import com.project.dogwalker.exception.member.WalkerNotWritePriceException;
import com.project.dogwalker.exception.reserve.AlreadyUnLockException;
import com.project.dogwalker.exception.reserve.LockInterruptedException;
import com.project.dogwalker.exception.reserve.ReserveAlreadyException;
import com.project.dogwalker.exception.reserve.ReserveNotAvailableException;
import com.project.dogwalker.exception.reserve.ReserveRequestNotExistException;
import com.project.dogwalker.exception.unauth.RefreshTokenNotExistException;
import com.project.dogwalker.exception.unauth.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String LOG_ERROR_MESSAGE = "Class : {}, Code : {}, Message : {}";

  @ExceptionHandler(ImgUploadFailException.class)
  public ResponseEntity<ExceptionResponse> handleImgException(final ImgUploadFailException e) {
    log.info(LOG_ERROR_MESSAGE , e.getClass() , e.getErrorCode() , e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignBadRequestException.class)
  public ResponseEntity<ExceptionResponse> handleFeignBadRequest(final FeignBadRequestException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignCommonException.class)
  public ResponseEntity<ExceptionResponse> handleFeignCommon(final FeignCommonException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignErrorParseException.class)
  public ResponseEntity<ExceptionResponse> handleFeignParse(final FeignErrorParseException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleFeignNotFound(final FeignNotFoundException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(FeignServerException.class)
  public ResponseEntity<ExceptionResponse> handleFeignServer(final FeignServerException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.internalServerError().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(WalkerNotWritePriceException.class)
  public ResponseEntity<ExceptionResponse> handleWalkerNotWritePRice(final WalkerNotWritePriceException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(LoginMemberNotFoundException.class)
  public ResponseEntity<TokenResponse> hanldeNotFoundMember(final LoginMemberNotFoundException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(TokenResponse.from(e,e.getToken()));
  }

  @ExceptionHandler(TokenExpiredException.class)
  public ResponseEntity<ExceptionResponse> handleTokenExpired(final TokenExpiredException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(AlreadyUnLockException.class)
  public ResponseEntity<ExceptionResponse> handleAlreadyUnLock(final AlreadyUnLockException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(LockInterruptedException.class)
  public ResponseEntity<ExceptionResponse> handleLockInterrupt(final LockInterruptedException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(ReserveAlreadyException.class)
  public ResponseEntity<ExceptionResponse> handleReserveAlready(final ReserveAlreadyException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(ReserveNotAvailableException.class)
  public ResponseEntity<ExceptionResponse> handleReserveNotAvaliable(final ReserveNotAvailableException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }
  @ExceptionHandler(ReserveRequestNotExistException.class)
  public ResponseEntity<ExceptionResponse> handleReserveRequestNotExist(final ReserveRequestNotExistException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse.from(e));
  }

  @ExceptionHandler(RefreshTokenNotExistException.class)
  public ResponseEntity<ExceptionResponse> handleRefreshTokenNotExistToken(final RefreshTokenNotExistException e){
    log.info(LOG_ERROR_MESSAGE,e.getClass(),e.getErrorCode(),e.getErrorMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ExceptionResponse.from(e));
  }

}

