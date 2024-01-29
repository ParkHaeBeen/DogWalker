package com.project.dogwalker.batch;

import static com.project.dogwalker.domain.reserve.PayStatus.ADJUST_DONE;
import static com.project.dogwalker.domain.reserve.PayStatus.PAY_DONE;
import static com.project.dogwalker.domain.reserve.PayStatus.PAY_REFUND;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_REFUSE;

import com.project.dogwalker.batch.adjust.dto.AdjustWalkerInfo;
import com.project.dogwalker.batch.reserve.dto.ReserveInfo;
import com.project.dogwalker.domain.adjust.AdjustStatus;
import com.project.dogwalker.domain.adjust.WalkerAdjust;
import com.project.dogwalker.domain.adjust.WalkerAdjustDetail;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
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
  public Job adjustWalkerFee(){
    return new JobBuilder("adjustJob",jobRepository)
        .start(adjustStep())
        .build();
  }

  @Bean
  @JobScope
  public Step reserveStep(){

    return new StepBuilder("reserveStep",jobRepository)
        .<ReserveInfo,WalkerReserveServiceInfo>chunk(chunkSize,platformManager)
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
  public ItemProcessor<ReserveInfo,WalkerReserveServiceInfo> reserveProcessor(){
    return reserveService -> {
      final WalkerReserveServiceInfo reserveServiceInfo = reserveService.getReserveServiceInfo();
      reserveServiceInfo.modifyStatus(WALKER_REFUSE);
      final PayHistory payHistory = reserveService.getPayHistory();
      payHistory.modifyStatus(PAY_REFUND);
      entityManager.merge(payHistory);
      return reserveServiceInfo;
    };
  }

  @Bean
  public JpaItemWriter<WalkerReserveServiceInfo> reserveWriter(){
    return new JpaItemWriterBuilder<WalkerReserveServiceInfo>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }

  @Bean
  @JobScope
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
    parameter.put("payStatus", PAY_DONE);

    JpaPagingItemReader<AdjustWalkerInfo> reader = new JpaPagingItemReader <>(){
      @Override
      public int getPage() {
        return 0;
      }
    };

    reader.setName("adjustReader");
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setPageSize(chunkSize);
    reader.setQueryString("SELECT NEW com.project.dogwalker.batch.adjust.dto.AdjustWalkerInfo(u, ph, w) "
        + "FROM PayHistory ph "
        + "JOIN FETCH ph.walkerReserveInfo w "
        + "JOIN User u On w.walker.userId = u.userId "
        + "WHERE w.status = :status "
        + "AND ph.payStatus = :payStatus");
    reader.setParameterValues(parameter);
    return reader;

  }

  @Bean
  public ItemProcessor<AdjustWalkerInfo, WalkerAdjust> adjustProcessor(){
    return adjustWalkerInfo -> {
      final Long userId=adjustWalkerInfo.getWalker().getUserId();
      final WalkerAdjust walkerAdjust=findOrCreateWalkerAdjust(userId);
      walkerAdjust.modifyWalkerTtlPrice(walkerAdjust.getWalkerTtlPrice()+adjustWalkerInfo.getPayHistory()
          .getPayPrice());

      final WalkerAdjustDetail adjustDetail=WalkerAdjustDetail.builder()
          .walkerAdjustPrice(adjustWalkerInfo.getPayHistory().getPayPrice())
          .walkerAdjust(walkerAdjust)
          .walkerReserveServiceId(adjustWalkerInfo.getReserveServiceInfo().getReserveId())
          .build();

      walkerAdjust.addAdjustDetail(adjustDetail);

      final PayHistory payHistory = adjustWalkerInfo.getPayHistory();
      payHistory.modifyStatus(ADJUST_DONE);
      entityManager.merge(payHistory);
      return walkerAdjust;
    };
  }

  private WalkerAdjust findOrCreateWalkerAdjust(final Long walkerId) {
    LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

    try {
      Query query = entityManager.createQuery("SELECT wa FROM WalkerAdjust wa " +
          "WHERE wa.userId = :walkerId AND wa.walkerAdjustDate = :currentDate");
      query.setParameter("walkerId", walkerId);
      query.setParameter("currentDate", LocalDate.now());

      WalkerAdjust result = (WalkerAdjust) query.getSingleResult();
      return result;
    } catch (NoResultException e) {
      return WalkerAdjust.builder()
          .userId(walkerId)
          .walkerAdjustDate(LocalDate.now())
          .walkerAdjustStatus(AdjustStatus.ADJUST_NOT_YET)
          .walkerAdjustPeriodStart(startOfMonth)
          .walkerAdjustPeriodEnd(endOfMonth)
          .build();
    }
  }

  @Bean
  public JpaItemWriter<WalkerAdjust> adjustWriter(){
    return new JpaItemWriterBuilder<WalkerAdjust>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
