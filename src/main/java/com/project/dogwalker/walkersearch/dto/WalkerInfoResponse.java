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
public class WalkerInfoResponse {

  private Long id;
  private String walkerName;
  private Double walkerLnt;
  private Double walkerLat;

  public static WalkerInfoResponse of(WalkerDocument document){
    return WalkerInfoResponse.builder()
        .id(document.getId())
        .walkerLat(document.getLocation().getLat())
        .walkerLnt(document.getLocation().getLon())
        .walkerName(document.getWalker_name())
        .build();
  }
}
