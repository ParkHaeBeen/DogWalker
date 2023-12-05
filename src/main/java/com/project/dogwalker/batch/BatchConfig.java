package com.project.dogwalker.batch;

import static com.project.dogwalker.domain.reserve.PayStatus.PAY_REFUND;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_REFUSE;

import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.reserve.service.ReserveService;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
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
  private final EntityManagerFactory entityManagerFactory;

  private int chunkSize=10;

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
        .<WalkerReserveServiceInfo,WalkerReserveServiceInfo>chunk(chunkSize,platformManager)
        .reader(reserveReader())
        .processor(reserveProcessor())
        .writer(reserveWriter())
        .build();

  }

  @Bean
  public JpaPagingItemReader<WalkerReserveServiceInfo> reserveReader(){
    Map<String, Object> parameter=new HashMap<>();
    parameter.put("createdAt", LocalDateTime.now().minusMinutes(10));
    parameter.put("status", WALKER_CHECKING);
    return new JpaPagingItemReaderBuilder<WalkerReserveServiceInfo>()
        .name("reserveReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString("SELECT w FROM WalkerReserveServiceInfo w "
            + "WHERE w.createdAt < :createdAt "
            + "AND w.status = :status")
        .parameterValues(parameter)
        .build();
  }

  @Bean
  public ItemProcessor<WalkerReserveServiceInfo,WalkerReserveServiceInfo> reserveProcessor(){
    return reserveService -> {
      reserveService.setStatus(WALKER_REFUSE);
      reserveService.getPayHistory().setPayStatus(PAY_REFUND);
      return entityManagerFactory.createEntityManager().merge(reserveService);
    };
  }

  @Bean
  public JpaItemWriter<WalkerReserveServiceInfo> reserveWriter(){
    return new JpaItemWriterBuilder<WalkerReserveServiceInfo>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }


}
