package com.project.dogwalker.notice.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.dto.NoticeRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NoticeService {

  SseEmitter addEmitter(MemberInfo memberInfo,String lastEventId);

  void send(NoticeRequest request);
}
