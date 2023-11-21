package com.project.dogwalker.member.controller;

import com.project.dogwalker.member.dto.join.JoinUserRequest;
import com.project.dogwalker.member.dto.join.JoinWalkerRequest;
import com.project.dogwalker.member.dto.LoginResponse;
import com.project.dogwalker.member.dto.LoginResult;
import com.project.dogwalker.member.service.OauthService;
import com.project.dogwalker.member.token.RefreshTokenCookieProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public ResponseEntity<String> getLoginView(@PathVariable String type){
    return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
        .body(oauthService.requestUrl(type));
  }


  /**
   * 새로운 고객이면 LoginResponse의 newMember 필드에 true가 들어가면서 회원가입 페이지로 이동
   * 가입된 고객이면 accessToken(바디)과 refreshToken(쿠키) 보냄
   * @param code
   * @param type
   */
  @GetMapping("/login/{type}")
  public ResponseEntity<LoginResponse> loginInfo(@RequestBody String code,@PathVariable String type){
    final LoginResult result = oauthService.login(code , type);
    final String refreshToken=result.getRefreshToken();

    //새로운 멤버시 회원가입뷰로 보내게
    if(refreshToken==null){
      return ResponseEntity.status(HttpStatus.OK)
          .body(LoginResponse.from(result));
    }

    //기존 회원 로그인시
    ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(result));
  }


  /**
   * 고객 회원가입
   * @param request
   */
  @PostMapping("/join/user")
  public ResponseEntity<LoginResponse> joinMember(@RequestBody JoinUserRequest request,@RequestPart MultipartFile dotImg){
    LoginResult joinResult = oauthService.joinCustomer(request , dotImg);

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
  public ResponseEntity<LoginResponse> joinWalker(@RequestBody JoinWalkerRequest request){
    LoginResult joinResult = oauthService.joinWalker(request);

    String refreshToken = joinResult.getRefreshToken();
    ResponseCookie cookie=refreshTokenCookieProvider.generateCookie(refreshToken);

    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.SET_COOKIE,cookie.toString())
        .body(LoginResponse.from(joinResult));
  }


}
