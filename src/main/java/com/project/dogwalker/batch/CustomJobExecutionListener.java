package com.project.dogwalker.batch;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomJobExecutionListener implements JobExecutionListener {

  @Override
  public void beforeJob(final JobExecution jobExecution) {
    log.info("job name : {}, ",jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(final JobExecution jobExecution) {
    final String jobName = jobExecution.getJobInstance().getJobName();
    final LocalDateTime startTime = jobExecution.getStartTime();
    final LocalDateTime endTime = jobExecution.getEndTime();
    log.info("job name : {}, startTime : {}, endTime : {}",jobName,startTime,endTime);
  }
}
