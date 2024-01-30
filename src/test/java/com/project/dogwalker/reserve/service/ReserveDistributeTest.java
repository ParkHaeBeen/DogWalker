package com.project.dogwalker.reserve.service;

import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.reserve.ReserveException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import com.project.dogwalker.support.BatchTest;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReserveDistributeTest extends BatchTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReserveServiceImpl reserveService;

  @Autowired
  private EntityManager entityManager;

  @Test
  @DisplayName("예약 중복 진행시 분산락 기능 성공")
  void reserveService_success() throws InterruptedException {
    // Mock data
    LocalDateTime serviceReserve=LocalDateTime.of(2023,12,12,15,30);
    User customer= userRepository.findById(2L).get();
    User walker= userRepository.findById(1L).get();

    int numThreads = 50;

    CountDownLatch latch = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executorService.execute(() -> {
        try {
          MemberInfo member=MemberInfo.builder()
              .role(Role.USER)
              .email("haebing0309@gmail.com")
              .build();
          ReserveRequest request=ReserveRequest.builder()
              .walkerId(walker.getUserId())
              .timeUnit(30)
              .serviceDateTime(serviceReserve)
              .price(1000)
              .payMethod("card")
              .build();
          reserveService.reserveService(member, request);
        }catch (Exception e){
          Assertions.assertThat(e).isInstanceOf(ReserveException.class);
        }finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    WalkerReserveServiceInfo singleResult = entityManager.createQuery(
            "SELECT w FROM WalkerReserveServiceInfo w "
                + "JOIN fetch w.customer "
                + "JOIN fetch w.walker " +
                "WHERE w.walker.userId = :walkerId " +
                "AND w.serviceDateTime = :serviceDate" , WalkerReserveServiceInfo.class)
        .setParameter("walkerId" , walker.getUserId())
        .setParameter("serviceDate" , serviceReserve)
        .getSingleResult();
    System.out.println(singleResult);
    Assertions.assertThat(singleResult.getServiceDateTime()).isEqualTo(serviceReserve);
    Assertions.assertThat(singleResult.getCustomer().getUserEmail()).isEqualTo(customer.getUserEmail());
    Assertions.assertThat(singleResult.getWalker().getUserEmail()).isEqualTo(walker.getUserEmail());

  }
}
