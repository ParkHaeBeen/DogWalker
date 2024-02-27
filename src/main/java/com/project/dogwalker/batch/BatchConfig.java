package com.project.dogwalker.batch;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class  BatchConfig{

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformManager;
  private final EntityManagerFactory entityManagerFactory;

  @PersistenceContext
  private EntityManager entityManager;
  private int chunkSize=10;


  @Bean
  public Job refuseReserveJob(@Qualifier("Reserve") Step reserveStep){

    return new JobBuilder("reserveJob",jobRepository)
        .start(reserveStep)
        .build();
  }

  @Bean
  public Job adjustJob(@Qualifier("Adjust") Step adjustStep, @Qualifier("AdjustDetail")
  Step adjustDetailStep) {
    return new JobBuilder("adjustJob" , jobRepository)
        .start(adjustStep)
        .next(adjustDetailStep)
        .build();
  }



}
