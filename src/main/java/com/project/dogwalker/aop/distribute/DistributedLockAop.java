package com.project.dogwalker.aop.distribute;

import static com.project.dogwalker.exception.ErrorCode.ALREADY_UNLOCK;
import static com.project.dogwalker.exception.ErrorCode.LOCK_INTERRUPTED_ERROR;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_NOT_AVAILABLE;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_REQUEST_NOT_EXIST;

import com.project.dogwalker.exception.reserve.LockException;
import com.project.dogwalker.exception.reserve.ReserveException;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;

  @Around("@annotation(com.project.dogwalker.aop.distribute.DistributedLock)")
  public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable{
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    final DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
    String key=getReserveParameter(joinPoint);
    RLock lock=redissonClient.getLock(key);


    try{
      boolean isLocked=lock.tryLock(distributedLock.waitTime(),distributedLock.leaseTime(),distributedLock.timeUnit());

      if(!isLocked){
        throw  new ReserveException(RESERVE_NOT_AVAILABLE);
      }
      log.info("redis lock start");

      return aopForTransaction.proceed(joinPoint);
    }catch (InterruptedException e){
      throw new LockException(LOCK_INTERRUPTED_ERROR);
    }finally {
      try {
        lock.unlock();
        log.info("redis unlock");

      }catch (IllegalMonitorStateException e){
        throw  new LockException(ALREADY_UNLOCK);
      }
    }

  }

  private String getReserveParameter(final ProceedingJoinPoint joinPoint) {
    final Object[] args = joinPoint.getArgs();
    ReserveRequest request=null;
    for (Object arg : args) {
      if(arg instanceof ReserveRequest){
        request=(ReserveRequest) arg;
        break;
      }
    }

    if(request==null){
      throw new ReserveException(RESERVE_REQUEST_NOT_EXIST);
    }

    return request.getClass().getSimpleName()+":"+request.getWalkerId()+":"+request.getServiceDateTime();
  }
}
