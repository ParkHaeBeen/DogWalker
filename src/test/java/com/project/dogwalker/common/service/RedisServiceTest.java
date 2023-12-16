package com.project.dogwalker.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisServiceTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private RedisTemplate<String,Object> objectRedisTemplate;
  @Autowired
  private ObjectMapper objectMapper;
  private final String startServicePrefix="start-";
  private final String proceedServicePrefix="proceed-";

  @Test
  void setData() throws JsonProcessingException {
    String key=startServicePrefix+1;
    String value="ON";
    int timeUnit=30;
    redisTemplate.opsForValue().set(key,value,timeUnit);

    String isStarted = redisTemplate.opsForValue().get(key);

    if(isStarted !=null){
      System.out.println(objectMapper.writeValueAsString(isStarted));
    }
    redisTemplate.delete(key);
  }

  @Test
  void getStartData() {
  }

  @Test
  void addToList() throws JsonProcessingException {

    String key=proceedServicePrefix+1;
    redisTemplate.delete(key);
    Coordinate coordinate1=new Coordinate(12.0,3.0);
    Coordinate coordinate2=new Coordinate(15.0,12.0);
    objectRedisTemplate.opsForList().rightPush(key,objectMapper.writeValueAsString(coordinate1));
    objectRedisTemplate.opsForList().rightPush(key,objectMapper.writeValueAsString(coordinate2));
    SimpleModule module = new SimpleModule();
    module.addDeserializer(Coordinate.class, new CoordinateDeserializer(Coordinate.class));
    objectMapper.registerModule(module);
    RedisOperations <String, Object > operations = objectRedisTemplate.opsForList().getOperations();
    List <Object > list = operations.opsForList().range(key , 0 , -1);
    for (Object o : list) {
      System.out.println("o = " + o);
    }
    List <Coordinate> collect = list.stream()
        .map(obj -> {
          try {
            return objectMapper.readValue(obj.toString(),Coordinate.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(Collectors.toList());
    System.out.println(list.size()+" "+collect.size());
    
    for (Coordinate coordinate : collect) {
      System.out.println(coordinate.toString());
    }
    redisTemplate.delete(key);

  }

  @Test
  void getList() {

  }

  @Test
  void deleteRedisData() {
  }
}