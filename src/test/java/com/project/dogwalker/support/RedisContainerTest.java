package com.project.dogwalker.support;

import org.junit.jupiter.api.DisplayName;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DisplayName("Redis Test Containers")
@ActiveProfiles(profiles = "test")
@Configuration
public class RedisContainerTest {
  static final String REDIS_IMAGE = "redis:7.0.8-alpine";
  @Container
  static final GenericContainer <?> REDIS_CONTAINER;

  static {
    REDIS_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
        .withExposedPorts(6379)
        .withReuse(true);
    REDIS_CONTAINER.start();
    System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
    System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
  }

}
