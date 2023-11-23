package com.project.dogwalker.member.token;

import com.project.dogwalker.domain.token.RefreshToken;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider {

  @Value("${security.refresh.expire-length}")
  private long REFRESH_EXPIRE_TIME;

  public RefreshToken generateRefreshToken(final Long userId){
    final LocalDateTime now = LocalDateTime.now();
    final LocalDateTime validateDate=now.plus(REFRESH_EXPIRE_TIME, ChronoUnit.MILLIS);

    return RefreshToken.builder()
        .refreshToken(UUID.randomUUID().toString())
        .expiredAt(validateDate)
        .userId(userId)
        .build();
  }

  public boolean isNotExpired(final RefreshToken refreshToken){
    return !refreshToken.getExpiredAt().isBefore(LocalDateTime.now());
  }

}
