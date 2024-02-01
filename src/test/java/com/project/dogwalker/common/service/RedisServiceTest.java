package com.project.dogwalker.common.service;

import com.project.dogwalker.common.service.redis.RedisService;
import com.project.dogwalker.support.RepositoryTest;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
@RepositoryTest
public class RedisServiceTest {
  private final String startServicePrefix="start-";
  private final String proceedServicePrefix="proceed-";

  @Autowired
  private RedisService redisService;

  @Test
  @DisplayName("위치 저장 및 조회 - 성공")
  void locationList_add_get_success(){
    //given
    String key=proceedServicePrefix+1;
    Coordinate coordinate1=new Coordinate(12.0,3.0);
    Coordinate coordinate2=new Coordinate(15.0,12.0);
    redisService.addToList(key,coordinate1);
    redisService.addToList(key,coordinate2);
    //when
    List <Coordinate> list = redisService.getList(key);

    //then
    Assertions.assertThat(list.size()).isEqualTo(2);
    redisService.deleteRedis(key);
  }

  @Test
  @DisplayName("서비스 시작 및 조회- 성공")
  void startService_add_get_success(){
    //given
    String key=startServicePrefix+1;
    String value="ON";
    int timeUnit=30;
    redisService.setData(key,value,timeUnit);
    //when
    boolean isStarted = redisService.getStartData(key);

    //then
    Assertions.assertThat(isStarted).isEqualTo(true);
    redisService.deleteRedis(key);
  }


  @Test
  @DisplayName("서비스 시작 및 조회- 실패")
  void startService_add_get_fail(){
    //given
    String key=startServicePrefix+1;
    //when
    boolean isStarted = redisService.getStartData(key);

    //then
    Assertions.assertThat(isStarted).isEqualTo(false);
    redisService.deleteRedis(key);
  }
}
