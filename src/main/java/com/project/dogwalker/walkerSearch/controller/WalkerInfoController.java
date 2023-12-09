package com.project.dogwalker.walkerSearch.controller;

import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkerSearch.service.WalkerInfoService;
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

  @GetMapping("/list")
  public ResponseEntity<?> getWalkerInfoList(@RequestBody final WalkerInfoSearchCond searchCond){
    return ResponseEntity.ok(walkerInfoService.getWalkerInfoList(searchCond));
  }

  @GetMapping("/detail")
  public ResponseEntity<?> getWalkerDetail(@RequestBody Long walkerId){
    return ResponseEntity.ok().build();
  }

}
