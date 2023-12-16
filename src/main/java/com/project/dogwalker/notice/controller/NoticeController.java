package com.project.dogwalker.notice.controller;

import com.project.dogwalker.member.controller.AuthMember;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class NoticeController {

  private final NoticeService noticeService;

  @GetMapping(value = "/connect",produces = "text/event-stream")
  public ResponseEntity<String> subscribeSse(@AuthMember MemberInfo memberInfo,
        @RequestParam(value = "lastEventId",required = false,defaultValue = "")final String lastEventId){
    noticeService.addEmitter(memberInfo,lastEventId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/read/{id}")
  public ResponseEntity<Void> readNotification(@PathVariable Long id) {
    noticeService.readNotification(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
