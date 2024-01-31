package com.project.dogwalker.walkerservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.dogwalker.common.service.route.dto.Location;
import java.time.LocalDateTime;
import java.util.List;
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

  private List <Location> routes;
}
