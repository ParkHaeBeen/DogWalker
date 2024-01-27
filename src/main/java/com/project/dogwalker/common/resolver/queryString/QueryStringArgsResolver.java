package com.project.dogwalker.common.resolver.queryString;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class QueryStringArgsResolver implements HandlerMethodArgumentResolver {

  private final ObjectMapper objectMapper;

  @Override
  public boolean supportsParameter(final MethodParameter parameter) {
    return parameter.getParameterAnnotation(QueryStringResolver.class) != null;
  }

  @Override
  public Object resolveArgument(final MethodParameter parameter , final ModelAndViewContainer mavContainer ,
      final NativeWebRequest webRequest , final WebDataBinderFactory binderFactory) throws Exception {

    final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
    final String queryStringToJson = queryStringToJson(request.getQueryString());
    final Object json = objectMapper.readValue(queryStringToJson, parameter.getParameterType());
    return json;
  }

  private String queryStringToJson(String queryString){
    String result = "{\"";

    for(int i = 0 ;i<queryString.length(); i++){
      if(queryString.charAt(i) == '='){
        result += "\"" + ":" + "\"";
      }else if(queryString.charAt(i)=='&'){
        result += "\"" + "," + "\"";
      }else {
        result += queryString.charAt(i);
      }
    }
    result += "\"" + "}";
    return result;
  }
}
