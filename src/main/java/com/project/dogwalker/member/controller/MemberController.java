package com.project.dogwalker.member.controller;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.unauth.TokenException;
import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.service.OauthService;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

  private final OauthService oauthService;
  private final RefreshTokenCookieProvider refreshTokenCookieProvider;

  @GetMapping("/login/{type}/view")
  public ResponseEntity<String> getLoginView(@PathVariable final String type){
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .body(oauthService.requestUrl(type));
  }


  /**
   * 새로운 회원이면 exception발생
   * @param code
   * @param type
   */
  @GetMapping("/login/{type}")
  public ResponseEntity<LoginResponse> loginInfo(@RequestParam final String code,@PathVariable final String type){
    final LoginResult result = oauthService.login(code , type);
    final String refreshToken=result.getRefreshToken();

    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(result));
  }


  /**
   * 고객 회원가입
   * @param joinRequest
   */
  @PostMapping( "/user")
  public ResponseEntity<LoginResponse> joinMember(@RequestPart @Valid final JoinUserRequest joinRequest
      ,@RequestPart(name = "dogImg") final MultipartFile dogImg){
    final LoginResult joinResult = oauthService.joinCustomer(joinRequest , dogImg);

    final String refreshToken = joinResult.getRefreshToken();
    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(joinResult));
  }

  /**
   * 서비스 수행자 회원가입
   * @param request
   */
  @PostMapping("/walker")
  public ResponseEntity<LoginResponse> joinWalker(@RequestBody @Valid final JoinWalkerRequest request){
    final LoginResult joinResult = oauthService.joinWalker(request);

    final String refreshToken = joinResult.getRefreshToken();
    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(joinResult));
  }

  /**
   * access token 만료시 accessToken,refreshToken 재지급
   * @param refreshToken
   */
  @PostMapping("/auth/newtoken")
  public ResponseEntity<String> getNewToken(@CookieValue(value = "RefreshToken",required = false) final String refreshToken){
    if(refreshToken==null||refreshToken.isEmpty()){
      throw new TokenException(ErrorCode.TOKEN_NOT_EXIST);
    }

    final IssueToken issueToken = oauthService.generateToken(refreshToken);
    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(issueToken.getRefreshToken());
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(issueToken.getAccessToken());
  }

  /**
   * refreshToken 만료시 프론트단에서 재발급 요청
   * @param accessToken
   */
  @PostMapping("/auth/refreshtoken")
  public ResponseEntity<Void> reIssueRefreshToken(@RequestBody final String accessToken){
    final String newRefreshToken = oauthService.generateNewRefreshToken(accessToken);
    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(newRefreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString()).build();
  }
}
