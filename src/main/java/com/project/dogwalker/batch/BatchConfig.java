package com.project.dogwalker.batch;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_REFUSE;

import com.project.dogwalker.batch.adjust.dto.AdjustWalkerInfo;
import com.project.dogwalker.domain.adjust.AdjustStatus;
import com.project.dogwalker.domain.adjust.WalkerAdjust;
import com.project.dogwalker.domain.adjust.WalkerAdjustDetail;
import com.project.dogwalker.domain.adjust.WalkerAdjustRepository;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayStatus;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
  private final EntityManagerFactory entityManagerFactory;
  private final WalkerAdjustRepository adjustRepository;

  @PersistenceContext
  private EntityManager manager;
  private int chunkSize=10;

  @Bean
  @JobScope
  public Job refuseReserveJob(){

    return new JobBuilder("reserveJob",jobRepository)
        .start(reserveStep())
        .build();
  }

  @Bean
  @JobScope
  public Job adjustWalkerFee(){
    return new JobBuilder("adjustJob",jobRepository)
        .start(adjustStep())
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
            + "Join Fetch w.payHistory p "
            + "WHERE w.createdAt < :createdAt "
            + "AND w.status = :status")
        .parameterValues(parameter)
        .build();
  }

  @Bean
  public ItemProcessor<WalkerReserveServiceInfo,WalkerReserveServiceInfo> reserveProcessor(){
    return reserveService -> {
      System.out.println("dddddddddd");
      reserveService.setStatus(WALKER_REFUSE);
      PayHistory payHistory = reserveService.getPayHistory();
      payHistory.setPayStatus(PayStatus.PAY_REFUND);
      System.out.println(payHistory.getPayStatus()+" "+payHistory.getPayId());
      return entityManagerFactory.createEntityManager().merge(reserveService);


    };
  }

  @Bean
  public JpaItemWriter<WalkerReserveServiceInfo> reserveWriter(){;
    return new JpaItemWriterBuilder<WalkerReserveServiceInfo>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }

  @Bean
  @StepScope
  public Step adjustStep(){

    return new StepBuilder("adjustStep",jobRepository)
        .<AdjustWalkerInfo, WalkerAdjust>chunk(chunkSize,platformManager)
        .reader(adjustReader())
        .processor(adjustProcessor())
        .writer(adjustWriter())
        .transactionManager(platformManager)
        .build();

  }

  @Bean
  public JpaPagingItemReader<AdjustWalkerInfo> adjustReader(){
    Map<String, Object> parameter = new HashMap<>();
    parameter.put("status", WalkerServiceStatus.FINISH);
    parameter.put("payStatus", PayStatus.PAY_DONE);

    return new JpaPagingItemReaderBuilder<AdjustWalkerInfo>()
        .name("adjustReader")
        .entityManagerFactory(entityManagerFactory)
        .pageSize(chunkSize)
        .queryString("SELECT NEW com.project.dogwalker.batch.adjust.dto.AdjustWalkerInfo(u, ph, w) "
        + "FROM WalkerReserveServiceInfo w "
        + "JOIN FETCH w.payHistory ph "
        + "JOIN FETCH w.walker u "
        + "WHERE w.status = :status "
        + "AND ph.payStatus = :payStatus")
        .parameterValues(parameter)
        .build();
  }

  @Bean
  public ItemProcessor<AdjustWalkerInfo, WalkerAdjust> adjustProcessor(){
    return adjustWalkerInfo -> {
      log.info("adjustWalkerInfo = {} " ,adjustWalkerInfo);
      Long userId=adjustWalkerInfo.getWalker().getUserId();
      log.info("-------findOrCreteWalker-------");
      WalkerAdjust walkerAdjust=findOrCreateWalkerAdjust(userId);
      log.info("-----findOrCreteWalker END------");
      walkerAdjust.setWalkerTtlPrice(walkerAdjust.getWalkerTtlPrice()+adjustWalkerInfo.getPayHistory()
          .getPayPrice());
      log.info("---------payHistory");
      WalkerAdjustDetail adjustDetail=WalkerAdjustDetail.builder()
          .walkerAdjustPrice(adjustWalkerInfo.getPayHistory().getPayPrice())
          .walkerAdjust(walkerAdjust)
          .walkerReserveServiceId(adjustWalkerInfo.getReserveServiceInfo().getReserveId())
          .build();
      walkerAdjust.addAdjustDetail(adjustDetail);
      log.info("walkerAdjust = {} " ,walkerAdjust);
      PayHistory payHistory = adjustWalkerInfo.getPayHistory();
      payHistory.setPayStatus(PayStatus.ADJUST_DONE);
      log.info("--------- payHistory End");
      manager.merge(payHistory);
      log.info("-------- payHistory ?");
      return manager.merge(walkerAdjust);
    };
  }

  private WalkerAdjust findOrCreateWalkerAdjust(final Long walkerId) {
    LocalDate startOfMonth = LocalDate.now().with(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
    LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    Optional <WalkerAdjust> walkerAdjustDate = adjustRepository.findByUserIdAndAndWalkerAdjustDate(
        walkerId , LocalDate.now());

    log.info("walkerAdjustDate = {} ", walkerAdjustDate);
    return walkerAdjustDate.orElseGet( ()->{
         return WalkerAdjust.builder()
              .userId(walkerId)
              .walkerAdjustDate(LocalDate.now())
              .walkerAdjustStatus(AdjustStatus.ADJUST_NOT_YET)
              .walkerAdjustPeriodStart(startOfMonth)
              .walkerAdjustPeriodEnd(endOfMonth)
              .build();
    });
  }

  @Bean
  public JpaItemWriter<WalkerAdjust> adjustWriter(){
    return new JpaItemWriterBuilder<WalkerAdjust>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
