package com.project.dogwalker.batch.reserve;

import com.project.dogwalker.batch.BatchConfig;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.batch.ReserveBatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchReserveScheduler {

  private final JobLauncher jobLauncher;
  private final BatchConfig batchConfig;

  @Scheduled(fixedRate = 300000)
  public void myScheduleMethod(){

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time", System.currentTimeMillis())
        .toJobParameters();

    try {
      jobLauncher.run(batchConfig.refuseReserveJob(),jobParameters);
    } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
             | JobParametersInvalidException e) {
      throw new ReserveBatchException(ErrorCode.BATCH_RESERVE_ERROR,e);
    }

  }
}
