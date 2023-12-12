package com.project.dogwalker.walkerSearch.dto;

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
public class WalkerUnAvailDetail {
  private String walkerName;
  private double lat;
  private double lnt;

  private List <WalkerPermUnAvailDate> permUnAvailDates;
  private List<WalkerTempUnAvailDate> tempUnAvailDates;
  private List<WalkerTimePrice> timePrices;

}
