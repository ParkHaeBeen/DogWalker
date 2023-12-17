package com.project.dogwalker.batch;

import static org.assertj.core.api.Assertions.assertThat;

import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import java.time.LocalDateTime;
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


@SpringBatchTest
@SpringBootTest
public class ReserveBatchTest {

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

  @Test
  @DisplayName("예약 배치 기능 성공 - 10분 후 예약상태 변경안된거 변경성절")
  public void reserveBatchJob_success() throws Exception {
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
        .userEmail("batchuser3@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser3")
        .userRole(Role.WALKER)
        .build();

    userRepository.save(user);
    userRepository.save(walker);

    WalkerReserveServiceInfo reserveServiceInfo1 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now())
        .timeUnit(60)
        .status(WalkerServiceStatus.WALKER_CHECKING)
        .servicePrice(100)
        .build();
    WalkerReserveServiceInfo reserveServiceInfo2 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now().plusDays(1))
        .timeUnit(60)
        .status(WalkerServiceStatus.WALKER_CHECKING)
        .servicePrice(100)
        .build();

    WalkerReserveServiceInfo saveService1 = reserveServiceRepository.save(reserveServiceInfo1);
    WalkerReserveServiceInfo saveService2 = reserveServiceRepository.save(reserveServiceInfo2);
    PayHistory payHistory1 = PayHistory.builder()
        .customer(user)
        .reserveService(saveService1)
        .payPrice(100)
        .payMethod("Credit Card")
        .build();
    PayHistory payHistory2 = PayHistory.builder()
        .customer(user)
        .reserveService(saveService2)
        .payPrice(100)
        .payMethod("Credit Card")
        .build();
    payHistoryRespository.save(payHistory1);
    payHistoryRespository.save(payHistory2);
    saveService1.setCreatedAt(LocalDateTime.now().minusMinutes(20));
    saveService2.setCreatedAt(LocalDateTime.now().minusMinutes(20));
    reserveServiceRepository.saveAndFlush(saveService1);
    reserveServiceRepository.saveAndFlush(saveService2);
    JobParameters jobParameters=new JobParametersBuilder()
        .addString("jobName","reserveJob")
        .addLong("time",System.currentTimeMillis())
        .toJobParameters();

    //when
    jobLauncherTestUtils.setJob(batchConfig.refuseReserveJob());
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);


    //then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
  }

}
