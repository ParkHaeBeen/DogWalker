package com.project.dogwalker.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.dogwalker.domain.adjust.WalkerAdjust;
import com.project.dogwalker.domain.adjust.WalkerAdjustRepository;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.PayStatus;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBatchTest
@SpringBootTest
public class WalkerAdjustBatchTest {
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  private BatchConfig batchConfig;

  @Autowired
  private WalkerReserveServiceRepository reserveServiceRepository;

  @Autowired
  private PayHistoryRespository payHistoryRespository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private WalkerAdjustRepository adjustRepository;

  @Test
  @DisplayName("정산 batch 기능 수행 - 성공 : Walkeradjust 엔티티가 있으면 거기에 추가")
  @Rollback(value = false)
  public void adjustBatch_success() throws Exception {
    //given
    User user= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("batchuser1@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(Role.WALKER)
        .build();
    User walker= User.builder()
        .userId(2L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("batchuser2@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser2")
        .userRole(Role.WALKER)
        .build();

    userRepository.save(user);
    userRepository.save(walker);

    WalkerReserveServiceInfo reserveServiceInfo1 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now())
        .timeUnit(50)
        .status(WalkerServiceStatus.FINISH)
        .servicePrice(1000)
        .build();

    WalkerReserveServiceInfo reserveServiceInfo2 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now().plusDays(1))
        .timeUnit(50)
        .status(WalkerServiceStatus.FINISH)
        .servicePrice(1000)
        .build();
    WalkerReserveServiceInfo reserveServiceInfo3 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now().plusDays(1))
        .timeUnit(50)
        .status(WalkerServiceStatus.CUSTOMER_CANCEL)
        .servicePrice(1000)
        .build();
    WalkerReserveServiceInfo saveService1 = reserveServiceRepository.save(reserveServiceInfo1);
    WalkerReserveServiceInfo saveService2 = reserveServiceRepository.save(reserveServiceInfo2);
    WalkerReserveServiceInfo saveService3 = reserveServiceRepository.save(reserveServiceInfo3);
    PayHistory payHistory1 = PayHistory.builder()
        .customer(user)
        .reserveService(saveService1)
        .payPrice(1000)
        .payMethod("CARD")
        .build();
    PayHistory payHistory2 = PayHistory.builder()
        .customer(user)
        .reserveService(saveService2)
        .payPrice(1000)
        .payMethod("CARD")
        .build();
    PayHistory payHistory3 = PayHistory.builder()
        .customer(user)
        .reserveService(saveService3)
        .payPrice(1000)
        .payStatus(PayStatus.PAY_REFUND)
        .payMethod("CARD")
        .build();
    LocalDate startOfMonth = LocalDate.now().with(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
    LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

    WalkerAdjust adjust=WalkerAdjust.builder()
        .walkerAdjustDate(LocalDate.now())
        .userId(walker.getUserId())
        .walkerTtlPrice(1000L)
        .walkerAdjustPeriodEnd(endOfMonth)
        .walkerAdjustPeriodStart(startOfMonth)
        .build();

    payHistoryRespository.save(payHistory1);
    payHistoryRespository.save(payHistory2);
    payHistoryRespository.save(payHistory3);
    adjustRepository.saveAndFlush(adjust);


    JobParameters jobParameters=new JobParametersBuilder()
        .addString("jobName","adjustJob")
        .addLong("time",System.currentTimeMillis())
        .toJobParameters();

    //when
    jobLauncherTestUtils.setJob(batchConfig.adjustWalkerFee());
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);


    //then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }
}
