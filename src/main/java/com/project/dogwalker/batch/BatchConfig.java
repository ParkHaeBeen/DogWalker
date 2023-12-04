package com.project.dogwalker.batch;

import com.project.dogwalker.batch.reserve.ReserveTasklet;
import com.project.dogwalker.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformManager;
  private final ReserveService reserveService;

  @Bean
  public Job refuseReserveJob(){

    return new JobBuilder("reserveJob",jobRepository)
        .start(reserveStep(jobRepository,platformManager))
        .build();
  }

  @Bean
  public Step reserveStep(JobRepository jobRepository,  PlatformTransactionManager platformTransactionManager){

    return new StepBuilder("reserveStep",jobRepository)
        .tasklet(new ReserveTasklet(reserveService),platformManager)
        .build();

  }


}
