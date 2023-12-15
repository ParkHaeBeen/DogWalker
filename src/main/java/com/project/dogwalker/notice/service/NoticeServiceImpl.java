package com.project.dogwalker.notice.service;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.notice.SseException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

  private final Map <String, SseEmitter> emitters = new ConcurrentHashMap <>();
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  @Override
  public SseEmitter addEmitter(final String email) {
    SseEmitter emitter=new SseEmitter(DEFAULT_TIMEOUT);
    emitters.put(email,emitter);

    emitter.onTimeout(()->emitters.remove(emitter));
    emitter.onCompletion(()->emitters.remove(emitter));
    return emitter;
  }

  public void sendReservationEvent(final String clientId,final String message){
    SseEmitter emitter = emitters.get(clientId);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().data(message, MediaType.TEXT_PLAIN));
      } catch (IOException e) {
        emitter.complete();
        emitters.remove(clientId);
        throw new SseException(ErrorCode.SSE_ERROR);
      }
    }
  }
}
