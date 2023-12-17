package com.project.dogwalker.domain.notice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository{
  public final Map<String, SseEmitter> emitters = new ConcurrentHashMap <>();
  private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

  public SseEmitter save(String id, SseEmitter sseEmitter) {
    emitters.put(id, sseEmitter);
    return sseEmitter;
  }

  public void saveEventCache(String id, Object event) {
    eventCache.put(id, event);
  }

  public Map<String, SseEmitter> findAllStartWithByEmail(String id) {
    return emitters.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(id))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Map<String, Object> findAllEventCacheStartWithEmail(String id) {
    return eventCache.entrySet().stream()
        .filter(entry -> entry.getKey().startsWith(id))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public void deleteAllStartWithEmail(String id) {
    emitters.forEach(
        (key, emitter) -> {
          if (key.startsWith(id)) {
            emitters.remove(key);
          }
        }
    );
  }

  public void deleteByEmail(String id) {
    emitters.remove(id);
  }

  public void deleteAllEventCacheStartWithEmail(String id) {
    eventCache.forEach(
        (key, data) -> {
          if (key.startsWith(id)) {
            eventCache.remove(key);
          }
        }
    );
  }
}
