package com.project.dogwalker.reserve.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.reserve.ReserveAlreadyException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
  @DisplayName("예약 중복 진행시 분산락 기능 성공")
  void reserveService_success() throws InterruptedException {
    // Mock data
    LocalDateTime serviceReserve=LocalDateTime.of(2023,12,12,12,30);
    User customer= User.builder()
        .userRole(Role.USER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("dalbeen")
        .userPhoneNumber("010-1234-1234")
        .userEmail("ddddd@gmail.com")
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

    int numThreads = 50;

    CountDownLatch latch = new CountDownLatch(numThreads);
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
    for (int i = 0; i < numThreads; i++) {
      executorService.execute(() -> {
        try {
          MemberInfo member=MemberInfo.builder()
              .role(Role.USER)
              .email("ddddd@gmail.com")
              .build();
          ReserveRequest request=ReserveRequest.builder()
              .walkerId(walkerSave.getUserId())
              .timeUnit(30)
              .serviceDateTime(serviceReserve)
              .price(1000)
              .payMethod("card")
              .build();
          reserveService.reserveService(member, request);
        }catch (Exception e){
          Assertions.assertThat(e).isInstanceOf(ReserveAlreadyException.class);
        }finally {
          latch.countDown();
        }
      });
    }

    latch.await();

    List<WalkerReserveServiceInfo> all = walkerReserveServiceRepository.findAll();
    WalkerReserveServiceInfo serviceDate = walkerReserveServiceRepository.findByWalkerUserIdAndServiceDateTime(
        walkerSave.getUserId() ,serviceReserve).get();
    assertThat(serviceDate.getServiceDateTime()).isEqualTo(serviceReserve);
    assertThat(serviceDate.getCustomer().getUserEmail()).isEqualTo(customer.getUserEmail());
    assertThat(walkerReserveServiceRepository.findAll().size()).isEqualTo(1);

  }
}
