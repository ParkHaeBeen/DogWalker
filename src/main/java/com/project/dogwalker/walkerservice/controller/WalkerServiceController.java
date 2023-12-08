package com.project.dogwalker.walkerservice.controller;

import com.project.dogwalker.member.controller.Auth;
import com.project.dogwalker.member.controller.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkerservice.dto.RealTimeLocation;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;
import com.project.dogwalker.walkerservice.service.WalkerService;
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
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class WalkerServiceController {

  private final WalkerService walkerService;

  /**
   * 서비스 시작전에 해당 예약존재하는지
   * 해당 walker가 수행해야하는 예약이 맞는지 확인
   * 예약 수행하는 날짜가 맞는지
   */
  @GetMapping("/check/service/valid")
  @Auth(isWalker = true)
  public ResponseEntity<?> checkReserveAndWalker(@AuthMember final MemberInfo memberInfo,@RequestBody final
      ServiceCheckRequest request){
    walkerService.checkService(memberInfo,request);

    return ResponseEntity.ok().build();
  }

  /**
   * 산책 예약자 서비스 시작되었는지 확인
   */
  @GetMapping("/check/service/start")
  @Auth
  public ResponseEntity<?> checkServiceStart(@RequestBody final Long reserveId){
    final boolean startedService = walkerService.isStartedService(reserveId);
    if(startedService){
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }


  /**
   * 프론트 쪽에서 10초마다 해당 위치에 대해서 엔드포인트 호출
   */
  @PostMapping("/location")
  public ResponseEntity<?> realTimeLocation(@RequestBody final RealTimeLocation location){
    walkerService.saveRealTimeLocation(location);
    return ResponseEntity.ok().build();
  }

  /**
   * 서비스 완료후 이동경로 내역 저장
   */
  @PostMapping("/finish")
  @Auth(isWalker = true)
  public ResponseEntity<ServiceEndResponse> endService(@AuthMember final MemberInfo memberInfo, @RequestBody
      ServiceEndRequest request){
    final ServiceEndResponse serviceEndResponse = walkerService.saveServiceRoute(memberInfo , request);
    return ResponseEntity.ok(serviceEndResponse);
  }
}
