package com.project.dogwalker.domain.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@AllArgsConstructor
@Table(name = "refresh_token")
@NoArgsConstructor
public class RefreshToken {

  @Id
  @Column(name = "refresh_token",nullable = false)
  private String refreshToken;

  @Column(name = "refresh_token_user_id",nullable = false)
  private Long userId;

  @Column(name = "refresh_token_expired_at",nullable = false)
  private LocalDateTime expiredAt;

}
