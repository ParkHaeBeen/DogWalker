package com.project.dogwalker.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUtils {

  private final RedisTemplate<String,Object> redisTemplate;

  public void setData(String key, String value,Long expiredTime){
    redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
  }

  // Redis 리스트에 데이터 추가
  public void addToList(String key, String value) {
    redisTemplate.opsForList().rightPush(key,value);
  }

  // Redis 리스트에서 데이터 조회
  public List<Object> getList(String key) {
    final RedisOperations<String, Object> routes = redisTemplate.opsForList().getOperations();
    return routes.opsForList().range(key,0,-1);
  }

  public void deleteRoutes(String key){
    redisTemplate.delete(key);
  }
}
