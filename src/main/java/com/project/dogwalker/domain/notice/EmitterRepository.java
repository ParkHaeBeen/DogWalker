package com.project.dogwalker.domain.notice;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {
  SseEmitter save(String emitterId, SseEmitter sseEmitter);

  void saveEventCache(String eventCacheId, Object event);

  Map<String, SseEmitter> findAllStartWithByEmail(String userId);

  Map <String, Object> findAllEventCacheStartWithEmail(String userId);

  void deleteAllStartWithEmail(String id);

  void deleteByEmail(String userId);

  void deleteAllEventCacheStartWithEmail(String userId);
}
