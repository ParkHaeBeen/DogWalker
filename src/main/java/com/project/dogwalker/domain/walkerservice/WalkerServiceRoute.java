package com.project.dogwalker.domain.walkerservice;

import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Builder
@Getter
@Entity
@AllArgsConstructor
@Table(name = "walker_service_route")
@NoArgsConstructor
public class WalkerServiceRoute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "walker_service_route_id",nullable = false)
  private Long routeId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "walker_reserve_service_id")
  private WalkerReserveServiceInfo reserveInfo;

  @Column(name = "walker_route", columnDefinition = "LINESTRING")
  private String routes;

  @CreatedDate
  private LocalDateTime createdAt;
}
