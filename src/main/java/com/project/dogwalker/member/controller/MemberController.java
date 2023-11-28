package com.project.dogwalker.member.controller;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.unauth.RefreshTokenNotExistException;
import com.project.dogwalker.member.dto.IssueToken;
import com.project.dogwalker.member.dto.LoginResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.service.OauthService;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
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
@RequestMapping("/api")
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
    log.info("login result = {}",code);
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
  @PostMapping( "/join/user")
  public ResponseEntity<LoginResponse> joinMember(@RequestPart("joinRequest")final JoinUserRequest joinRequest,@RequestPart("dogImg") final MultipartFile dogImg){
    log.info("joinrequest ={}",joinRequest);
    LoginResult joinResult = oauthService.joinCustomer(joinRequest , dogImg);

    String refreshToken = joinResult.getRefreshToken();
    ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(joinResult));
  }

  /**
   * 서비스 수행자 회원가입
   * @param request
   */
  @PostMapping("/join/walker")
  public ResponseEntity<LoginResponse> joinWalker(@RequestBody final JoinWalkerRequest request){
    log.info("join walker request = {}",request);
    LoginResult joinResult = oauthService.joinWalker(request);

    String refreshToken = joinResult.getRefreshToken();
    ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(joinResult));
  }

  /**
   * token 만료시 지급
   * @param refreshToken
   */
  @PostMapping("/auth/newtoken")
  public ResponseEntity<?> getNewToken(@CookieValue(value = "RefreshToken",required = false) final String refreshToken){
    if(refreshToken==null){
      throw new RefreshTokenNotExistException(ErrorCode.NOT_EXIST_REFRESH_TOKEN);
    }

    final IssueToken issueToken = oauthService.generateToken(refreshToken);
    final ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(issueToken.getRefreshToken());
    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(issueToken.getAccessToken());
  }


}
