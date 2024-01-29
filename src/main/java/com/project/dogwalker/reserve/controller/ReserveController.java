package com.project.dogwalker.reserve.controller;

import com.project.dogwalker.common.interceptor.Auth;
import com.project.dogwalker.common.resolver.auth.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.dto.ReserveStatusRequest;
import com.project.dogwalker.reserve.service.ReserveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/reserve")
@RequiredArgsConstructor
public class ReserveController {

  private final ReserveService reserveService;


  /**
   * 결제 뷰로 넘어가기전에 해당 날짜에 예약이 되어있는지 확인
   */
  @GetMapping("/check")
  @Auth
  public ResponseEntity<?> isReservedCheck(@RequestBody @Valid final ReserveCheckRequest request){
    reserveService.isReserved(request);
    return ResponseEntity.ok().build();
  }

  /**
   * 예약및 결제 진행
   */
  @PostMapping
  @Auth
  public ResponseEntity<ReserveResponse> reserveService(@AuthMember @Valid final MemberInfo memberInfo,@RequestBody @Valid final ReserveRequest request){
    final ReserveResponse reserveResponse = reserveService.reserveService(memberInfo , request);
    return ResponseEntity.ok(reserveResponse);
  }

  /**
   * 서비스 수행자 예약 요청 수락/거부
   */
  @PatchMapping("/request")
  @Auth(isWalker = true)
  public ResponseEntity<?> changeRequestServiceStatus(@AuthMember @Valid final MemberInfo memberInfo,@RequestBody @Valid ReserveStatusRequest request){
    reserveService.changeRequestServiceStatus(memberInfo,request);
    return ResponseEntity.ok().build();
  }
  /**
   * 예약 하루전까지 취소 가능
   */
  @PostMapping("/cancel")
  @Auth
  public ResponseEntity<?> reserveCancel(@AuthMember @Valid final MemberInfo memberInfo, @RequestBody @Valid final ReserveCancel.Request request ){
    return ResponseEntity.ok(reserveService.reserveCancel(memberInfo,request));
  }
}
