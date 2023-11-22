package com.project.dogwalker.config;

import com.project.dogwalker.exception.feign.FeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ImportAutoConfiguration({FeignAutoConfiguration.class})
@EnableFeignClients(basePackages = "com.project.dogwalker.member")
public class FeignConfig {
  @Bean
  public ErrorDecoder errorDecoder(){
    return new FeignErrorDecoder();
  }
}
