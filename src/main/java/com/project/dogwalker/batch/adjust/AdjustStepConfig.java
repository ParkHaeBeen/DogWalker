package com.project.dogwalker.batch.adjust;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.FINISH;

import com.project.dogwalker.domain.adjust.WalkerAdjust;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class AdjustStepConfig {

  private final int chunkSize=10;
  private final EntityManagerFactory entityManagerFactory;


  @Bean (name = "Adjust")
  @JobScope
  public Step adjustStep(JobRepository jobRepository , PlatformTransactionManager transactionManager) {
    return new StepBuilder("adjustStep" , jobRepository)
        .<Long, WalkerAdjust>chunk(chunkSize ,transactionManager)
        .reader(adjustItemReader())
        .processor(adjustItemProcessor())
        .writer(adjustItemWriter())
        .build();
  }

  @Bean
  public ItemReader<Long> adjustItemReader() {
    Map <String, Object> parameters = new HashMap <>();
    parameters.put("status", FINISH);
    JpaPagingItemReader <Long> reader = new JpaPagingItemReader <>();
    reader.setName("adjustReader");
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString("SELECT u.userId FROM User u "
        + "JOIN WalkerReserveServiceInfo w ON u.userId = w.walker.userId "
        + "where w.status = :status "
        + "Group By u.userId ORDER BY u.userId");

    reader.setPageSize(chunkSize);
    reader.setParameterValues(parameters);
    return reader;
  }

  @Bean
  public ItemProcessor <Long, WalkerAdjust> adjustItemProcessor() {
    return userId -> {
      final LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
      final LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
      return WalkerAdjust.builder()
          .userId(userId)
          .walkerAdjustDate(LocalDate.now())
          .walkerAdjustPeriodStart(startOfMonth)
          .walkerAdjustPeriodEnd(endOfMonth)
          .build();
    };
  }

  @Bean
  public ItemWriter <WalkerAdjust> adjustItemWriter(){
    return new JpaItemWriterBuilder <WalkerAdjust>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
