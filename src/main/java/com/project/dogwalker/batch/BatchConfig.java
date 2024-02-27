package com.project.dogwalker.batch;

import static com.project.dogwalker.domain.reserve.PayStatus.PAY_REFUND;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_REFUSE;

import com.project.dogwalker.batch.reserve.dto.ReserveInfo;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
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
  public Job refuseReserveJob(){

    return new JobBuilder("reserveJob",jobRepository)
        .start(reserveStep())
        .build();
  }

  @Bean
  public Job adjustJob(@Qualifier("Adjust") Step adjustStep, @Qualifier("AdjustDetail")
  Step adjustDetailStep) {
    return new JobBuilder("adjust" , jobRepository)
        .start(adjustStep)
        .next(adjustDetailStep)
        .build();
  }

  @Bean
  @JobScope
  public Step reserveStep(){

    return new StepBuilder("reserveStep",jobRepository)
        .<ReserveInfo,PayHistory>chunk(chunkSize,platformManager)
        .reader(reserveReader())
        .processor(reserveProcessor())
        .writer(reserveWriter())
        .build();

  }

  @Bean
  public JpaPagingItemReader<ReserveInfo> reserveReader(){
    Map<String, Object> parameter=new HashMap<>();
    parameter.put("createdAt", LocalDateTime.now().minusMinutes(10));
    parameter.put("status", WALKER_CHECKING);
    JpaPagingItemReader<ReserveInfo> reader = new JpaPagingItemReader <>(){
      @Override
      public int getPage() {
        return 0;
      }
    };
    reader.setName("reserveReader");
    reader.setPageSize(chunkSize);
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setQueryString("SELECT NEW com.project.dogwalker.batch.reserve.dto.ReserveInfo(p, w) FROM PayHistory p "
        + "Join Fetch p.walkerReserveInfo w "
        + "WHERE w.createdAt < :createdAt "
        + "AND w.status = :status");
    reader.setParameterValues(parameter);
    return reader;
  }

  @Bean
  public ItemProcessor<ReserveInfo,PayHistory> reserveProcessor(){
    return reserveService -> {
      final WalkerReserveServiceInfo reserveServiceInfo = reserveService.getReserveServiceInfo();
      reserveServiceInfo.modifyStatus(WALKER_REFUSE);
      final PayHistory payHistory = reserveService.getPayHistory();
      payHistory.modifyStatus(PAY_REFUND);
      return payHistory;
    };
  }

  @Bean
  public JpaItemWriter<PayHistory> reserveWriter(){
    return new JpaItemWriterBuilder<PayHistory>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }


}
