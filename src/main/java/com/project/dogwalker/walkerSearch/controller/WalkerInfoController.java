package com.project.dogwalker.walkerSearch.controller;

import com.project.dogwalker.member.controller.Auth;
import com.project.dogwalker.member.controller.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkerSearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerUnAvailDetail;
import com.project.dogwalker.walkerSearch.service.WalkerInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/walkerinfo")
@RequiredArgsConstructor
@RestController
public class WalkerInfoController {

  private final WalkerInfoService walkerInfoService;

  /**
   * walker list 기본으로 위치기준 검색 추가적으로 이름 검색 가능
   */
  @GetMapping("/list")
  @Auth
  public ResponseEntity<List <WalkerInfo>> getWalkerInfoList(@AuthMember final MemberInfo memberInfo
      ,@RequestBody final WalkerInfoSearchCond searchCond){
    return ResponseEntity.ok(walkerInfoService.getWalkerInfoList(memberInfo,searchCond));
  }

  /**
   * 서비스 수행자가 안되는 요일과 시간 + 일시적으로 안되는 날짜 + 단위 시간당 가격 전송
   */
  @GetMapping("/detail")
  public ResponseEntity<WalkerUnAvailDetail> getWalkerDetail(@RequestBody final Long walkerId){
    return ResponseEntity.ok(walkerInfoService.getWalkerUnAvailService(walkerId));
  }

  /**
   * 해당날짜에 예약이 있는 날짜 전송
   */
  @GetMapping("/detail/check/reserve")
  public ResponseEntity<List<WalkerReserveInfo.Response>> getWalkerAlreadyReserveDate(@RequestBody final WalkerReserveInfo.Request reserveInfo){
    return ResponseEntity.ok(walkerInfoService.getReserveDate(reserveInfo));
  }

}
