package com.project.dogwalker.exception.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.exception.dto.FeignResponseError;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

  private final ObjectMapper objectMapper;

  @Override
  public Exception decode(final String methodKey ,final Response response) {
    switch (response.status()){
      case 400 : return new FeignBadRequestException(parse(response));
      case 404 : return new FeignNotFoundException(parse(response));
      case 503 : return new FeignServerException(parse(response));
      default: return new FeignCommonException(parse(response));
    }
  }

  private String parse(final Response response){
    try {
      return objectMapper.readValue(response.body().asInputStream(), FeignResponseError.class).error.toString();
    } catch (IOException e) {
      throw new FeignErrorParseException(e);
    }
  }
}
