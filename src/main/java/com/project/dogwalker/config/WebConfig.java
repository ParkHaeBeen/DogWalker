package com.project.dogwalker.config;

import com.project.dogwalker.member.controller.AuthArgumentResolver;
import com.project.dogwalker.member.controller.AuthInterceptor;
import com.project.dogwalker.member.token.JwtTokenProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {


  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    registry.addInterceptor(new AuthInterceptor(jwtTokenProvider));
  }
  @Override
  public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new AuthArgumentResolver(jwtTokenProvider));
  }
}
