package com.project.dogwalker.walkerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ServiceEndResponse {

  private Long routeId;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime endTime;
}
