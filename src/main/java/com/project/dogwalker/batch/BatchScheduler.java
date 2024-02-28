package com.project.dogwalker.batch;

import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.batch.AdjustBatchException;
import com.project.dogwalker.exception.batch.ReserveBatchException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchScheduler {

  private final Job refuseReserveJob;
  private final Job adjustJob;
  private final JobLauncher jobLauncher;


  public BatchScheduler(@Qualifier ("adjustJob") final Job refuseReserveJob ,
      @Qualifier ("refuseReserveJob") final Job adjustJob,
      final JobLauncher jobLauncher) {
    this.refuseReserveJob = refuseReserveJob;
    this.adjustJob = adjustJob;
    this.jobLauncher = jobLauncher;
  }

  @Scheduled (fixedRate = 300000)
  public void refuseReserveJobSchedule() {

    JobParameters jobParameters = new JobParametersBuilder()
        .addLong("time" , System.currentTimeMillis())
        .toJobParameters();

    try {
      jobLauncher.run(refuseReserveJob , jobParameters);
    } catch (JobExecutionAlreadyRunningException | JobRestartException |
             JobInstanceAlreadyCompleteException
             | JobParametersInvalidException e) {
      throw new ReserveBatchException(ErrorCode.BATCH_RESERVE_ERROR , e);
    }
  }

  @Scheduled(cron = "0 50 23 L * ?")
  public void adjustJobSchedule(){
    JobParameters jobParameters=new JobParametersBuilder()
        .addLong("time",System.currentTimeMillis())
        .toJobParameters();
    try {
      jobLauncher.run(adjustJob,jobParameters);
    }catch (JobExecutionAlreadyRunningException | JobRestartException |
            JobInstanceAlreadyCompleteException
            | JobParametersInvalidException e) {
      throw new AdjustBatchException(ErrorCode.BATCH_ADJUST_ERROR,e);
    }
  }

}
