package com.project.dogwalker.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String,Object> redisTemplate;
  private final ObjectMapper objectMapper;


  //Redis 서비스 시작시 데이터 추가
  public void setData(final String key,final String value,final int expiredTime){
    try {
      String data= Arrays.toString(value.getBytes("UTF-8"));
      redisTemplate.opsForValue().set(key, data, expiredTime, TimeUnit.MINUTES);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  //Redis 서비스 시작되었는지 확인
  public boolean getStartData(final String key){
    final Object isStarted = redisTemplate.opsForValue().get(key);
    if(isStarted !=null){
      return String.valueOf(isStarted).equals("ON");
    }

    return false;
  }

  // Redis 리스트에 데이터 추가
  public void addToList(final String key, final Coordinate coordinate) {
    redisTemplate.opsForList().rightPush(key,coordinate);
  }

  // Redis 리스트에서 데이터 조회
  public List<Coordinate> getList(final String key) {
    final RedisOperations<String, Object> routes = redisTemplate.opsForList().getOperations();
    final List <Object> list = routes.opsForList().range(key , 0 , -1);
    return list.stream()
        .filter(obj -> obj instanceof Coordinate)
        .map(obj->(Coordinate) obj)
        .collect(Collectors.toList());
  }

  public void deleteRedisData(final String key){
    redisTemplate.delete(key);
  }
}
