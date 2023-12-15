package com.project.dogwalker.notice.controller;

import com.project.dogwalker.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping(value = "/connect",produces = "text/event-stream")
  public ResponseEntity<String> sse(@RequestBody final String email){
    noticeService.addEmitter(email);
    return ResponseEntity.ok().build();
  }



}
