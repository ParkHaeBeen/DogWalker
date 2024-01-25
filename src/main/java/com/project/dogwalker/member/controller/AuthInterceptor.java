package com.project.dogwalker.member.controller;

import static com.project.dogwalker.exception.ErrorCode.NOT_WALKER;
import static com.project.dogwalker.exception.ErrorCode.TOKEN_EXPIRED;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.member.MemberException;
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
    final Auth authAnnotation = getLoginAnnotation(handler);

    if(!(handler instanceof HandlerMethod) || authAnnotation ==null){
      return true;
    }

    if(isAuthExist(request)){
      validateToken(request);
      isWalker(request, authAnnotation);
      return true;
    }

    validateTokenRequired(authAnnotation);
    return true;
  }

  private void validateTokenRequired(final Auth authAnnotation) {
    if(authAnnotation !=null && authAnnotation.required()){
      throw new TokenNotExistException(ErrorCode.TOKEN_NOT_EXIST);
    }
  }

  private void isWalker(final HttpServletRequest request , final Auth authAnnotation) {
    if(authAnnotation !=null && authAnnotation.isWalker() && !checkWalker(request)){
      throw new MemberException(NOT_WALKER);
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

  private boolean isAuthExist(final HttpServletRequest request) {
    return request.getHeader(HttpHeaders.AUTHORIZATION) != null;
  }

  private Auth getLoginAnnotation(final Object handler) {
    if (handler instanceof HandlerMethod) {
      HandlerMethod handlerMethod = (HandlerMethod) handler;
      return handlerMethod.getMethodAnnotation(Auth.class);
    }
    return null;
  }
}
