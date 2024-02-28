package com.project.dogwalker.batch.adjust;

import static com.project.dogwalker.domain.reserve.PayStatus.ADJUST_DONE;
import static com.project.dogwalker.domain.reserve.PayStatus.PAY_DONE;

import com.project.dogwalker.domain.adjust.WalkerAdjust;
import com.project.dogwalker.domain.adjust.WalkerAdjustDetail;
import com.project.dogwalker.domain.adjust.WalkerAdjustRepository;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.batch.AdjustBatchException;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
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
public class AdjustDetailStepConfig {

  private final WalkerAdjustRepository walkerAdjustRepository;
  private final EntityManagerFactory entityManagerFactory;
  private final int chunkSize=10;

  @Bean (name = "AdjustDetail")
  @JobScope
  public Step adjustDetailStep(JobRepository jobRepository , PlatformTransactionManager transactionManager) {
    return new StepBuilder("adjustDetailStep" , jobRepository)
        .<PayHistory, WalkerAdjustDetail>chunk(chunkSize,transactionManager)
        .reader(adjustDetailReader())
        .processor(adjustDetailProcessor())
        .writer(adjustDetailItemWriter())
        .build();
  }

  @Bean
  public ItemReader <PayHistory> adjustDetailReader() {
    Map <String, Object> parameters = new HashMap <>();
    parameters.put("serviceStatus", WalkerServiceStatus.FINISH);
    parameters.put("payStatus", PAY_DONE);
    JpaPagingItemReader <PayHistory> reader = new JpaPagingItemReader <>(){
      @Override
      public int getPage() {
        return 0;
      }
    };
    reader.setName("adjustDetailReader");
    reader.setEntityManagerFactory(entityManagerFactory);
    reader.setParameterValues(parameters);
    reader.setPageSize(chunkSize);
    reader.setQueryString("select p from PayHistory p "
        + "join fetch p.walkerReserveInfo w "
        + "JOIN User u On w.walker.userId = u.userId "
        + "where p.payStatus = :payStatus "
        + "and w.status = :serviceStatus");
    return reader;
  }

  @Bean
  public ItemProcessor <PayHistory, WalkerAdjustDetail> adjustDetailProcessor() {
    return payHistory -> {
      final WalkerAdjust walkerAdjust = walkerAdjustRepository.findByUserIdAndAndWalkerAdjustDate(
              payHistory.getWalkerReserveInfo().getWalker().getUserId() , LocalDate.now())
          .orElseThrow(() -> new AdjustBatchException(ErrorCode.BATCH_ADJUST_ERROR));
      walkerAdjust.modifyWalkerTtlPrice(walkerAdjust.getWalkerTtlPrice()+payHistory.getPayPrice());
      payHistory.modifyStatus(ADJUST_DONE);
      return WalkerAdjustDetail.builder()
          .walkerAdjust(walkerAdjust)
          .payHistory(payHistory)
          .walkerAdjustPrice(payHistory.getPayPrice())
          .build();

    };
  }

  @Bean
  public ItemWriter <WalkerAdjustDetail> adjustDetailItemWriter(){
    return new JpaItemWriterBuilder <WalkerAdjustDetail>()
        .entityManagerFactory(entityManagerFactory)
        .build();
  }
}
