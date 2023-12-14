package com.project.dogwalker.reserve.controller;

import com.project.dogwalker.member.controller.Auth;
import com.project.dogwalker.member.controller.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<?> isReservedCheck(@RequestBody final ReserveCheckRequest request){
    reserveService.isReserved(request);
    return ResponseEntity.ok().build();
  }

  /**
   * 예약및 결제 진행
   */
  @PostMapping
  @Auth
  public ResponseEntity<ReserveResponse> reserveService(@AuthMember final MemberInfo memberInfo,@RequestBody ReserveRequest request){
    final ReserveResponse reserveResponse = reserveService.reserveService(memberInfo , request);
    return ResponseEntity.ok(reserveResponse);
  }

  /**
   * 예약 하루전까지 취소 가능
   */
  @PostMapping("/cancel")
  @Auth
  public ResponseEntity<?> reserveCancel(@AuthMember final MemberInfo memberInfo, @RequestBody final ReserveCancel.Request request ){
    return ResponseEntity.ok(reserveService.reserveCancel(memberInfo,request));
  }
}
