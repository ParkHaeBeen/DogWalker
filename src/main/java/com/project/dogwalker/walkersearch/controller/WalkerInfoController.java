package com.project.dogwalker.walkersearch.controller;

import com.project.dogwalker.common.resolver.queryString.QueryStringResolver;
import com.project.dogwalker.member.controller.Auth;
import com.project.dogwalker.member.controller.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfoResponse;
import com.project.dogwalker.walkersearch.dto.WalkerInfoRequest;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.WalkerUnAvailDetailResponse;
import com.project.dogwalker.walkersearch.service.WalkerInfoService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/walkerinfo")
@RequiredArgsConstructor
@RestController
public class WalkerInfoController {

  private final WalkerInfoService walkerInfoService;

  /**
   * walker list 기본으로 위치기준 검색 추가적으로 이름 검색 가능
   */
  @GetMapping("/list")
  @Auth
  public ResponseEntity<List <WalkerInfoResponse>> getWalkerInfoList(@AuthMember @Valid final MemberInfo memberInfo
      ,@QueryStringResolver @Valid final WalkerInfoRequest searchCond, @PageableDefault final Pageable pageable){
    return ResponseEntity.ok(walkerInfoService.getWalkerInfoList(memberInfo, searchCond, pageable));
  }

  /**
   * 서비스 수행자가 안되는 요일과 시간 + 일시적으로 안되는 날짜 + 단위 시간당 가격 전송
   */
  @GetMapping
  public ResponseEntity<WalkerUnAvailDetailResponse> getWalkerDetail(@RequestParam final Long walkerId){
    return ResponseEntity.ok(walkerInfoService.getWalkerUnAvailService(walkerId));
  }

  /**
   * 해당날짜에 예약이 있는 날짜 전송
   */
  @GetMapping("/reserve")
  public ResponseEntity<List<WalkerReserveInfo.Response>> getWalkerAlreadyReserveDate(@RequestBody @Valid final WalkerReserveInfo.Request reserveInfo){
    return ResponseEntity.ok(walkerInfoService.getReserveDate(reserveInfo));
  }

}
