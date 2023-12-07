package com.project.dogwalker.domain.user.elastic;


import com.project.dogwalker.domain.user.User;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@Document(indexName = "walkers")
public class WalkerDocument {

  @Id
  @Field(type = FieldType.Long)
  private Long id;

  @Field(type = FieldType.Text)
  private String walkerName;

  @GeoPointField
  private GeoPoint location;

  public static WalkerDocument of(User user){
    GeoPoint geoPoint = new GeoPoint(user.getUserLat(),user.getUserLnt());
    return WalkerDocument.builder()
        .id(user.getUserId())
        .walkerName(user.getUserName())
        .location(geoPoint)
        .build();
  }
}
