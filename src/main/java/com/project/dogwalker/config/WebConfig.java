package com.project.dogwalker.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final List<HandlerInterceptor> interceptors;
  private final List<HandlerMethodArgumentResolver> resolvers;

  @Override
  public void addInterceptors(final InterceptorRegistry registry) {
    interceptors.forEach(registry::addInterceptor);
  }
}
