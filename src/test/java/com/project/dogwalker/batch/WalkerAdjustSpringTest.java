package com.project.dogwalker.batch;

import static com.project.dogwalker.domain.reserve.PayStatus.PAY_REFUND;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.CUSTOMER_CANCEL;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.FINISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.project.dogwalker.domain.adjust.WalkerAdjustDetailRepository;
import com.project.dogwalker.domain.adjust.WalkerAdjustRepository;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.support.RepositoryTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RepositoryTest
public class WalkerAdjustSpringTest {
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

  @Autowired
  private WalkerAdjustDetailRepository adjustDetailRepository;

  @Autowired
  @Qualifier("Adjust")
  private Step adjustStep;

  @Autowired
  @Qualifier("AdjustDetail")
  private Step adjustDetailStep;

  @Test
  @DisplayName("정산 batch 기능 수행 - 성공 : Walkeradjust 엔티티가 있으면 거기에 추가")
  public void adjustBatch_success() throws Exception {
    //given
    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("batchuser888@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(Role.USER)
        .build();
    User walker= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("batchuser999@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser2")
        .userRole(Role.WALKER)
        .build();

    userRepository.save(user);
    userRepository.save(walker);

    for(int i=0;i<100;i++){
      WalkerReserveServiceInfo reserveServiceInfo = WalkerReserveServiceInfo.builder()
          .customer(user)
          .walker(walker)
          .serviceDateTime(LocalDateTime.now().minusDays(i))
          .timeUnit(50)
          .status(FINISH)
          .servicePrice(1000)
          .build();
      reserveServiceRepository.save(reserveServiceInfo);

      PayHistory payHistory = PayHistory.builder()
          .customer(user)
          .payPrice(1000)
          .payMethod("CARD")
          .walkerReserveInfo(reserveServiceInfo)
          .build();

      payHistoryRespository.save(payHistory);

    }

    WalkerReserveServiceInfo reserveServiceInfo3 = WalkerReserveServiceInfo.builder()
        .customer(user)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now().minusHours(2))
        .timeUnit(50)
        .status(CUSTOMER_CANCEL)
        .servicePrice(1000)
        .build();

    reserveServiceRepository.save(reserveServiceInfo3);

    PayHistory payHistory3 = PayHistory.builder()
        .customer(user)
        .payPrice(1000)
        .payStatus(PAY_REFUND)
        .walkerReserveInfo(reserveServiceInfo3)
        .payMethod("CARD")
        .build();
    payHistoryRespository.save(payHistory3);

    JobParameters jobParameters=new JobParametersBuilder()
        .addString("jobName","adjustJob")
        .addLong("time",System.currentTimeMillis())
        .toJobParameters();

    //when
    jobLauncherTestUtils.setJob(batchConfig.adjustJob(adjustStep,adjustDetailStep));
    JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);


    //then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    assertThat(adjustRepository.findByUserIdAndAndWalkerAdjustDate(walker.getUserId(),LocalDate.now()).get().getUserId()).isEqualTo(walker.getUserId());
    assertThat(adjustDetailRepository.findAll().size()).isEqualTo(100);
  }
}
