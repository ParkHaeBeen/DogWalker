package com.project.dogwalker.walkersearch.dto;

import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class WalkerInfo {

  private String walkerName;
  private Double walkerLnt;
  private Double walkerLat;

  public static WalkerInfo of(WalkerDocument document){
    return WalkerInfo.builder()
        .walkerLat(document.getLocation().getLat())
        .walkerLnt(document.getLocation().getLon())
        .walkerName(document.getWalker_name())
        .build();
  }
}
