package com.project.dogwalker.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String redisHost;

  @Value("${spring.data.redis.port}")
  private int redisPort;

  private final ObjectMapper objectMapper;
  private static final String REDISSON_HOST_PREFIX="redis://";

  @Bean
  public RedissonClient redissonClient(){
    Config config=new Config();
    config.useSingleServer().setAddress(REDISSON_HOST_PREFIX+redisHost+":"+redisPort);
    return Redisson.create(config);
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory(){
    return new LettuceConnectionFactory(redisHost,redisPort);
  }

  @Bean
  public RedisTemplate<String,Object> redisTemplate(){
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    RedisSerializer <Object> jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

    RedisTemplate<String, Object > redisTemplate=new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(jsonSerializer);
    redisTemplate.setValueSerializer(jsonSerializer);
    return redisTemplate;
  }
  @Bean
  public StringRedisTemplate stringRedisTemplate(){
    StringRedisTemplate stringRedisTemplate=new StringRedisTemplate();
    stringRedisTemplate.setKeySerializer(new StringRedisSerializer());
    stringRedisTemplate.setValueSerializer(new StringRedisSerializer());
    stringRedisTemplate.setConnectionFactory(redisConnectionFactory());
    return stringRedisTemplate;
  }


}