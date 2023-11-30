package com.project.dogwalker.reserve.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Execution(ExecutionMode.CONCURRENT)
public class ReserveDistributeTest {

  @Autowired
  private WalkerReserveServiceRepository walkerReserveServiceRepository;
  @Autowired
  private PayHistoryRespository payHistoryRespository;
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ReserveServiceImpl reserveService;

  @Test
  @DisplayName("예약 진행-성공")
  void reserveService_success() throws InterruptedException {
    // Mock data
    MemberInfo member=MemberInfo.builder()
        .role(Role.USER)
        .email("ddd@gmail.com")
        .build();

    User customer= User.builder()
        .userRole(Role.USER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("dalbeen")
        .userPhoneNumber("010-1234-1234")
        .userEmail("ddd@gmail.com")
        .build();
    User walker=User.builder()
        .userRole(Role.WALKER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("walker")
        .userPhoneNumber("010-1234-1234")
        .userEmail("walker@gmail.com")
        .build();
    userRepository.save(customer);

    User walkerSave = userRepository.save(walker);
    ReserveRequest request=ReserveRequest.builder()
        .walkerId(walkerSave.getUserId())
        .timeUnit(30)
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .price(1000)
        .payMethod("card")
        .build();

    int numThreads = 10;

    CountDownLatch latch = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(100);
    for (int i = 0; i < numThreads; i++) {
      executorService.execute(() -> {
        try {
          ReserveResponse reserveResponse = reserveService.reserveService(member, request);
          System.out.println(reserveResponse.toString());
        } finally {
          latch.countDown();
        }
      });
    }

    latch.await();
    WalkerReserveServiceInfo serviceDate = walkerReserveServiceRepository.findByWalkerUserIdAndServiceDate(
        walkerSave.getUserId() , request.getServiceDate()).get();
    assertThat(serviceDate.getServiceDate()).isEqualTo(request.getServiceDate());
    assertThat(serviceDate.getCustomer().getUserEmail()).isEqualTo(customer.getUserEmail());
    assertThat(walkerReserveServiceRepository.findAll().size()).isEqualTo(1);

  }
}
