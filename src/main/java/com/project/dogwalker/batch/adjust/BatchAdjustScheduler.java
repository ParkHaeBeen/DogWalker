package com.project.dogwalker.batch.adjust;

import com.project.dogwalker.batch.BatchConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchAdjustScheduler {
  private final JobLauncher jobLauncher;
  private final BatchConfig batchConfig;

 // @Scheduled(cron = "0 50 23 L * ?")
/*  public void adjustMethod(){
    JobParameters jobParameters=new JobParametersBuilder()
        .addLong("time",System.currentTimeMillis())
        .toJobParameters();

    try {
      jobLauncher.run(,jobParameters);
    }catch (JobExecutionAlreadyRunningException | JobRestartException |
            JobInstanceAlreadyCompleteException
            | JobParametersInvalidException e) {
      throw new AdjustBatchException(ErrorCode.BATCH_ADJUST_ERROR,e);
    }
  }*/
}
