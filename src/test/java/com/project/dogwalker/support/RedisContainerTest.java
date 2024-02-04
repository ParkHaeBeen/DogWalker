package com.project.dogwalker.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisContainerTest {

  static final String REDIS_IMAGE = "redis:7.0.8-alpine";
  static final GenericContainer <?> REDIS_CONTAINER;

  static {
    REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
        .withExposedPorts(6379) // 여기서 6380은 호스트에 노출될 포트, 6379는 컨테이너 내부의 포트
        .withReuse(true);
    REDIS_CONTAINER.start(); // (4)
  }

  @DynamicPropertySource // (5)
  public static void overrideProps(DynamicPropertyRegistry registry){
    registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    registry.add("spring.data.redis.port", () -> ""+REDIS_CONTAINER.getMappedPort(6379));
  }
}
