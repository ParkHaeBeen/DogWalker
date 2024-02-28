package com.project.dogwalker.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class  BatchConfig{

  private final JobRepository jobRepository;
  private final JobExecutionListener jobExecutionListener;

  @Bean("refuseReserveJob")
  public Job refuseReserveJob(@Qualifier("Reserve") Step reserveStep){

    return new JobBuilder("reserveJob",jobRepository)
        .listener(jobExecutionListener)
        .start(reserveStep)
        .build();
  }

  @Bean("adjustJob")
  public Job adjustJob(@Qualifier("Adjust") Step adjustStep, @Qualifier("AdjustDetail")
  Step adjustDetailStep) {
    return new JobBuilder("adjustJob" , jobRepository)
        .listener(jobExecutionListener)
        .start(adjustStep)
        .next(adjustDetailStep)
        .build();
  }
}
