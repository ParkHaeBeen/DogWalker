package com.project.dogwalker.batch.adjust;

import com.project.dogwalker.batch.BatchConfig;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.batch.AdjustBatchException;
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
public class BatchAdjustScheduler {
  private final JobLauncher jobLauncher;
  private final BatchConfig batchConfig;

  @Scheduled(cron = "0 0 0 * * ?")
  public void adjustMethod(){
    JobParameters jobParameters=new JobParametersBuilder()
        .addLong("adjustBatch",System.currentTimeMillis())
        .toJobParameters();

    try {
      jobLauncher.run(batchConfig.adjustWalkerFee(),jobParameters);
    }catch (JobExecutionAlreadyRunningException | JobRestartException |
            JobInstanceAlreadyCompleteException
            | JobParametersInvalidException e) {
      throw new AdjustBatchException(ErrorCode.BATCH_ADJUST_ERROR,e);
    }
  }
}
