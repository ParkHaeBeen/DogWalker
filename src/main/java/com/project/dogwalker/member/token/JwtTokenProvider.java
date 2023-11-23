package com.project.dogwalker.member.token;

import com.project.dogwalker.domain.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value("${security.jwt.expire-length}")
  private long TOKEN_EXPIRE_TIME;

  @Value("${security.jwt.secret-key}")
  private String SECRET_KEY;
  public static final String TOKEN_PREFIX="Bearer ";
  private static final String ACCESS_SUBJECT="AccessToken";

  /**
   * accessToken 발급
   * @param email 회원이메일
   * @param role 회원 역할(고객, 워커)
   */
  public String generateToken(final String email,final Role role){
    final Date now=new Date();
    final Date validateDate=new Date(now.getTime()+TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setSubject(ACCESS_SUBJECT)
        .setIssuedAt(now)
        .setExpiration(validateDate)
        .claim("email",email)
        .claim("role",role)
        .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
        .compact();
  }

  /**
   * token accessToken인지 확인 + 만료기간 확인
   * @param authorizationToken
   */
  public boolean validateToken(final String authorizationToken){
    if (!StringUtils.hasText(authorizationToken)) return false;
    Jws<Claims> claims=parseClaims(authorizationToken);
    return isAccessToken(claims)&&isNotExpired(claims);
  }

  private Jws<Claims> parseClaims(final String authorizationToken) {
    return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(authorizationToken);
  }

  private boolean isAccessToken(Jws<Claims> claims) {
    return claims.getBody().getSubject().equals(ACCESS_SUBJECT);
  }

  private boolean isNotExpired(Jws<Claims> claims) {
    return claims.getBody().getExpiration().after(new Date());
  }

  public boolean isWalker(final String authorizationToken){
    Jws<Claims> claims = parseClaims(authorizationToken);
    Role role = (Role) claims.getBody().get("role");
    return role==Role.WALKER;
  }

}
