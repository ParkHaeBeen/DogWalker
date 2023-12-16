package com.project.dogwalker.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisConfigTest {

  @Autowired
  private RedisTemplate<String,Object> objectRedisTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  private final String startServicePrefix="start-";
  private final String proceedServicePrefix="proceed-";


  @Test
  @DisplayName("Redis에 서비스 시작했다고 저장 테스트 - 성공")
  void setServiceStart_success() throws JsonProcessingException {
    String key=startServicePrefix+1;
    String value="ON";
    int timeUnit=30;
    objectRedisTemplate.opsForValue().set(key,value,timeUnit);

    Object isStarted = objectRedisTemplate.opsForValue().get(key);

    Assertions.assertThat(isStarted).isNotNull();
    Assertions.assertThat(isStarted.toString().trim()).isEqualTo(value);
    objectRedisTemplate.delete(key);
  }

  @Test
  @DisplayName("Redis에 서비스 중 수행자 위치 저장 테스트 - 성공")
  void addLocation_success() throws JsonProcessingException {

    String key=proceedServicePrefix+1;
    objectRedisTemplate.delete(key);
    Coordinate coordinate1=new Coordinate(12.0,3.0);
    Coordinate coordinate2=new Coordinate(15.0,12.0);
    objectRedisTemplate.opsForList().rightPush(key,objectMapper.writeValueAsString(coordinate1));
    objectRedisTemplate.opsForList().rightPush(key,objectMapper.writeValueAsString(coordinate2));

    RedisOperations <String, Object > operations = objectRedisTemplate.opsForList().getOperations();
    List <Object > list = operations.opsForList().range(key , 0 , -1);

    List <Coordinate> collect = list.stream()
        .map(obj -> {
          try {
            return objectMapper.readValue(obj.toString(),Coordinate.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());

    Assertions.assertThat(collect.size()).isEqualTo(2);
    objectRedisTemplate.delete(key);

  }

}