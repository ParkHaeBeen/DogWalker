package com.project.dogwalker.reserve.controller;

import com.project.dogwalker.common.interceptor.Auth;
import com.project.dogwalker.common.resolver.auth.AuthMember;
import com.project.dogwalker.common.resolver.queryString.QueryStringResolver;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.request.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.response.ReserveListResponse;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import com.project.dogwalker.reserve.dto.response.ReserveResponse;
import com.project.dogwalker.reserve.dto.request.ReserveStatusRequest;
import com.project.dogwalker.reserve.service.ReserveService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {

  private final ReserveService reserveService;

  /**
   * 결제 뷰로 넘어가기전에 해당 날짜에 예약이 되어있는지 확인
   */
  @GetMapping("/check")
  @Auth
  public ResponseEntity<?> isReservedCheck(@QueryStringResolver @Valid final ReserveCheckRequest request){
    reserveService.isReserved(request);
    return ResponseEntity.ok().build();
  }

  /**
   * 예약및 결제 진행
   */
  @PostMapping
  @Auth
  public ResponseEntity<ReserveResponse> reserveService(@AuthMember @Valid final MemberInfo memberInfo
      ,@RequestBody @Valid final ReserveRequest request){
    final ReserveResponse reserveResponse = reserveService.reserveService(memberInfo , request);
    return ResponseEntity.ok(reserveResponse);
  }

  /**
   * 서비스 수행자 예약 요청 수락/거부
   */
  @PatchMapping
  @Auth(isWalker = true)
  public ResponseEntity<?> changeRequestServiceStatus(@AuthMember @Valid final MemberInfo memberInfo,
      @RequestBody @Valid ReserveStatusRequest request){
    reserveService.changeRequestServiceStatus(memberInfo,request);
    return ResponseEntity.ok().build();
  }

  /**
   * 예약 하루전까지 취소 가능
   */
  @DeleteMapping
  @Auth
  public ResponseEntity<ReserveCancel.Response> reserveCancel(@AuthMember @Valid final MemberInfo memberInfo,
      @RequestBody @Valid final ReserveCancel.Request request ){
    return ResponseEntity.ok(reserveService.reserveCancel(memberInfo,request));
  }

  /**
   * 예약 리스트 조회(user, walker에따라 조회가능)
   */
  @GetMapping
  @Auth
  public ResponseEntity<List <ReserveListResponse>> getReserveList(@AuthMember @Valid final MemberInfo memberInfo, final
  Pageable pageable){
    return ResponseEntity.ok(reserveService.getReserveList(memberInfo, pageable));
  }

  @GetMapping("/{reserveId}")
  @Auth
  public ResponseEntity<ReserveResponse> getReserveDetail(@AuthMember @Valid final MemberInfo memberInfo, @PathVariable final Long reserveId){
    return ResponseEntity.ok(reserveService.getReserveDetail(memberInfo, reserveId));
  }

}
