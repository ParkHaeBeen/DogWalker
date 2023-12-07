package com.project.dogwalker.aop.distribute;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.dogwalker.exception.reserve.ReserveNotAvailableException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.service.ReserveServiceImpl;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class DistributedLockAopTest {
  @Mock
  private AopForTransaction aopForTransaction;

  @Mock
  private RedissonClient redissonClient;

  @InjectMocks
  private DistributedLockAop distributedLockAop;

  @Test
  @DisplayName("aop 분산락 테스트 성공")
  void testLock_success() throws Throwable {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature=mock(MethodSignature.class);
    Method method = ReserveServiceImpl.class.getMethod("reserveService", MemberInfo.class ,
        ReserveRequest.class);
    DistributedLock distributedLockAnnotation = method.getAnnotation(DistributedLock.class);
    ReserveRequest reserveRequest = new ReserveRequest();
    reserveRequest.setWalkerId(10L);
    reserveRequest.setServiceDateTime(LocalDateTime.of(2023,12,12,15,30));
    RLock lock = mock(RLock.class);

    ReserveResponse reserveResponse=ReserveResponse.builder().build();
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(method);
    when(joinPoint.getArgs()).thenReturn(new Object[]{reserveRequest});
    when(redissonClient.getLock(anyString())).thenReturn(lock);
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
    when(aopForTransaction.proceed(joinPoint)).thenReturn(reserveResponse);
    //when
    distributedLockAop.lock(joinPoint);

    //then
    verify(lock, times(1)).tryLock(distributedLockAnnotation.waitTime(),
        distributedLockAnnotation.leaseTime(), distributedLockAnnotation.timeUnit());
    verify(lock, times(1)).unlock();
  }

  @Test
  @DisplayName("aop 분산락 테스트 실패 - locked = false")
  void testLock_fail() throws Throwable {
    //given
    ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
    MethodSignature methodSignature=mock(MethodSignature.class);
    Method method = ReserveServiceImpl.class.getMethod("reserveService", MemberInfo.class ,
        ReserveRequest.class);
    DistributedLock distributedLockAnnotation = method.getAnnotation(DistributedLock.class);
    ReserveRequest reserveRequest = new ReserveRequest();
    reserveRequest.setWalkerId(10L);
    reserveRequest.setServiceDateTime(LocalDateTime.of(2023,12,12,15,30));
    RLock lock = mock(RLock.class);

    ReserveResponse reserveResponse=ReserveResponse.builder().build();
    when(joinPoint.getSignature()).thenReturn(methodSignature);
    when(methodSignature.getMethod()).thenReturn(method);
    when(joinPoint.getArgs()).thenReturn(new Object[]{reserveRequest});
    when(redissonClient.getLock(anyString())).thenReturn(lock);
    when(lock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(false);


    //when
    //then
    Assertions.assertThrows(ReserveNotAvailableException.class,()->distributedLockAop.lock(joinPoint));
  }

}