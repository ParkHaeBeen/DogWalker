package com.project.dogwalker.domain.user.walker.elastic;


import com.project.dogwalker.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
@ToString
@Document(indexName = "walkers")
public class WalkerDocument {

  @Id
  private String id;

  @Field(type = FieldType.Long,name = "walker_info_id")
  private Long walker_info_id;

  @Field(type = FieldType.Text,name = "walker_name")
  private String walker_name;

  @GeoPointField
  private GeoPoint location;

  public static WalkerDocument of(User user){
    GeoPoint geoPoint = new GeoPoint(user.getUserLat(),user.getUserLnt());
    return WalkerDocument.builder()
        .walker_info_id(user.getUserId())
        .walker_name(user.getUserName())
        .location(geoPoint)
        .build();
  }
}
