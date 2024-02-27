package com.project.dogwalker.batch.reserve;

import static com.project.dogwalker.domain.reserve.PayStatus.PAY_REFUND;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_REFUSE;

import com.project.dogwalker.domain.reserve.PayHistory;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReserveStepConfig {

  private final EntityManagerFactory entityManagerFactory;
  private final int chunkSize=10;

  @Bean(name = "Reserve")
  @JobScope
  public Step reserveStep(JobRepository jobRepository , PlatformTransactionManager transactionManager) {
    return new StepBuilder("adjustDetailStep" , jobRepository)
        .<PayHistory, PayHistory>chunk(10,transactionManager)
        .reader(reserveReader())
        .processor(reserveProcessor())
        .writer(reserveWriter())
        .build();
  }

  @Bean
  public ItemReader<PayHistory> reserveReader() {
    Map <String, Object> parameters=new HashMap <>();
    parameters.put("createdAt", LocalDateTime.now().minusMinutes(10));
    parameters.put("status", WALKER_CHECKING);
    JpaPagingItemReader <PayHistory> reader = new JpaPagingItemReader <>(){
      @Override
      public int getPage() {
        return 0;
      }
    };
    reader.setName("reserveReader");
    reader.setPageSize(chunkSize);
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString("SELECT p FROM PayHistory p "
        + "Join Fetch p.walkerReserveInfo w "
        + "WHERE w.createdAt < :createdAt "
        + "AND w.status = :status");
    reader.setParameterValues(parameters);
    return reader;
  }

  @Bean
  public ItemProcessor <PayHistory,PayHistory> reserveProcessor(){
    return payHistory -> {
      payHistory.getWalkerReserveInfo().modifyStatus(WALKER_REFUSE);
      payHistory.modifyStatus(PAY_REFUND);
      return payHistory;
    };
  }

  @Bean
  public JpaItemWriter <PayHistory> reserveWriter(){
    return new JpaItemWriterBuilder <PayHistory>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
