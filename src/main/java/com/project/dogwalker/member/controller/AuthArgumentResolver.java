package com.project.dogwalker.member.controller;

import com.project.dogwalker.member.token.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtTokenProvider jwtTokenProvider;


  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AuthMember.class);

  }

  @Override
  public Object resolveArgument(final MethodParameter parameter , final ModelAndViewContainer mavContainer ,
      final NativeWebRequest webRequest ,final WebDataBinderFactory binderFactory) throws Exception {
    final String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

    if(authorizationHeader==null){
      return null;
    }
    return jwtTokenProvider.getMemberInfo(authorizationHeader);
  }
}
