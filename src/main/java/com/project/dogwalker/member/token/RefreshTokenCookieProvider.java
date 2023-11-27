package com.project.dogwalker.member.token;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieProvider {

  private static final String REFRESH_TOKEN="RefreshToken";

  @Value("${security.refresh.expire-length}")
  private long REFRESH_EXPIRE_TIME;

  public ResponseCookie generateCookie(final String refreshToken){
    return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .sameSite(SameSite.NONE.attributeValue())
        .maxAge(Duration.ofMillis(REFRESH_EXPIRE_TIME))
        .build();
  }

}
