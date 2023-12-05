package com.project.dogwalker.batch;

import com.project.dogwalker.batch.reserve.ReserveTasklet;
import com.project.dogwalker.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig{

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformManager;
  private final ReserveService reserveService;


  @Bean
  @JobScope
  public Job refuseReserveJob(){

    return new JobBuilder("reserveJob",jobRepository)
        .start(reserveStep())
        .build();
  }

  @Bean
  @StepScope
  public Step reserveStep(){

    return new StepBuilder("reserveStep",jobRepository)
        .tasklet(new ReserveTasklet(reserveService),platformManager)
        .build();

  }


}
