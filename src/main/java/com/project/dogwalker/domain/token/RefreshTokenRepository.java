package com.project.dogwalker.domain.token;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {

  Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
