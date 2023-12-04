package com.project.dogwalker.batch.reserve;

import com.project.dogwalker.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReserveTasklet implements Tasklet {

  private final ReserveService reserveService;

  @Override
  public RepeatStatus execute(StepContribution contribution , ChunkContext chunkContext)
      throws Exception {
      reserveService.changeReserveStatus();
    return null;
  }
}
