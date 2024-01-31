package com.project.dogwalker.walkerservice.controller;

import com.project.dogwalker.common.interceptor.Auth;
import com.project.dogwalker.common.resolver.auth.AuthMember;
import com.project.dogwalker.common.resolver.queryString.QueryStringResolver;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkerservice.dto.RealTimeLocation;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;
import com.project.dogwalker.walkerservice.service.WalkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/service")
@RequiredArgsConstructor
public class WalkerServiceController {

  private final WalkerService walkerService;

  /**
   * 서비스 시작전에 해당 예약존재하는지
   * 해당 walker가 수행해야하는 예약이 맞는지 확인
   * 예약 수행하는 날짜가 맞는지
   */
  @GetMapping("/valid")
  @Auth(isWalker = true)
  public ResponseEntity<?> checkReserveAndWalker(@AuthMember @Valid final MemberInfo memberInfo
      ,@QueryStringResolver final ServiceCheckRequest request){
    walkerService.checkService(memberInfo,request);

    return ResponseEntity.ok().build();
  }

  /**
   * 산책 예약자 서비스 시작되었는지 확인
   */
  @GetMapping("/start/{reserveId}")
  @Auth
  public ResponseEntity<Boolean> checkServiceStart(@PathVariable final Long reserveId){
    return ResponseEntity.ok(walkerService.isStartedService(reserveId));
  }


  /**
   * 프론트 쪽에서 10초마다 해당 위치에 대해서 엔드포인트 호출
   */
  @PostMapping
  public ResponseEntity<?> realTimeLocation(@RequestBody @Valid final RealTimeLocation location){
    walkerService.saveRealTimeLocation(location);
    return ResponseEntity.ok().build();
  }

  /**
   * 고객에게 서비스 완료 5분전이라 알림
   */
  @PostMapping("/notice/{reserveId}")
  @Auth(isWalker = true)
  public ResponseEntity<?> noticeCustomer(@PathVariable final Long reserveId){
    walkerService.noticeCustomer(reserveId);
    return ResponseEntity.ok().build();
  }
  /**
   * 서비스 완료후 이동경로 내역 저장
   */
  @PostMapping("/finish")
  @Auth(isWalker = true)
  public ResponseEntity<ServiceEndResponse> endService(@AuthMember @Valid final MemberInfo memberInfo,
      @RequestBody @Valid ServiceEndRequest request){
    final ServiceEndResponse serviceEndResponse = walkerService.saveServiceRoute(memberInfo , request);
    return ResponseEntity.ok(serviceEndResponse);
  }
}
