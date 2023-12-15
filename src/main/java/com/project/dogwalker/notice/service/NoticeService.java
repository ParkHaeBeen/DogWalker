package com.project.dogwalker.notice.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NoticeService {

  SseEmitter addEmitter(String email);

  void sendReservationEvent(String cliendId,String message);
}
