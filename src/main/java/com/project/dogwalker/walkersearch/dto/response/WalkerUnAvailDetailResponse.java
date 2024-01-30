package com.project.dogwalker.walkersearch.dto.response;

import com.project.dogwalker.walkersearch.dto.response.WalkerPermUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerTempUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerTimePriceResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class WalkerUnAvailDetailResponse {
  private String walkerName;
  private double lat;
  private double lnt;

  private List <WalkerPermUnAvailDateResponse> permUnAvailDates;
  private List<WalkerTempUnAvailDateResponse> tempUnAvailDates;
  private List<WalkerTimePriceResponse> timePrices;

}
