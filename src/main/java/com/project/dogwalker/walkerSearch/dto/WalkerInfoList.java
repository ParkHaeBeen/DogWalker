package com.project.dogwalker.walkerSearch.dto;

import com.project.dogwalker.domain.user.elastic.WalkerDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkerInfoList {

  private String walkerName;
  private Double walkerLnt;
  private Double walkerLat;

  public static WalkerInfoList of(WalkerDocument document){
    return WalkerInfoList.builder()
        .walkerLat(document.getLocation().getLat())
        .walkerLnt(document.getLocation().getLon())
        .walkerName(document.getWalker_name())
        .build();
  }
}
