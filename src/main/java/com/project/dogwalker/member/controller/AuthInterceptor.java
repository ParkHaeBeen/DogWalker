package com.project.dogwalker.member.controller;

import static com.project.dogwalker.exception.ErrorCode.NOT_WALKER;
import static com.project.dogwalker.exception.ErrorCode.TOKEN_EXPIRED;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.member.NotWalkerException;
import com.project.dogwalker.exception.unauth.TokenExpiredException;
import com.project.dogwalker.exception.unauth.TokenNotExistException;
import com.project.dogwalker.member.token.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Component
public class AuthInterceptor implements HandlerInterceptor {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public boolean preHandle(final HttpServletRequest request ,final HttpServletResponse response ,final Object handler) throws Exception {

    if(!(handler instanceof HandlerMethod) || checkLoginAnnotation(handler)==null){
      return true;
    }

    if(checkAuth(request)){
      validateToken(request);
      IsWalkerRequired(request,handler);
      return true;
    }

    validateTokenRequired(handler);
    return true;
  }

  private void validateTokenRequired(final Object handler) {
    Login login = checkLoginAnnotation(handler);
    if(login!=null && login.required()){
      throw new TokenNotExistException(ErrorCode.TOKEN_NOT_EXIST);
    }
  }

  private void IsWalkerRequired(final HttpServletRequest request , final Object handler) {
    Login login = checkLoginAnnotation(handler);
    if(login!=null && login.isWalker() && !checkWalker(request)){
      throw new NotWalkerException(NOT_WALKER);
    }
  }

  /**
   * 워커이면 true를 내보냄
   * @param request
   */
  private boolean checkWalker(final HttpServletRequest request) {
    return jwtTokenProvider.isWalker(request.getHeader(HttpHeaders.AUTHORIZATION));
  }

  private void validateToken(final HttpServletRequest request) {
    final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

    if(!jwtTokenProvider.validateToken(authHeader)){
      throw new TokenExpiredException(TOKEN_EXPIRED);
    }
  }

  private boolean checkAuth(final HttpServletRequest request) {
    return request.getHeader(HttpHeaders.AUTHORIZATION) != null;
  }

  private Login checkLoginAnnotation(final Object handler) {
    HandlerMethod handlerMethod= (HandlerMethod) handler;
    return handlerMethod.getMethodAnnotation(Login.class);
  }
}
