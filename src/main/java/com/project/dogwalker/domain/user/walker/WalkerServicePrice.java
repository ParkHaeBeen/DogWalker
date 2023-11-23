package com.project.dogwalker.domain.user.walker;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "walker_service_price")
public class WalkerServicePrice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "walker_price_id")
  private Long walkerPcId;

  @Column(name = "walker_id",nullable = false)
  private Long walkerId;

  @Column(name = "walker_service_unit",nullable = false)
  private Integer timeUnit;

  @Column(name = "walker_service_fee",nullable = false)
  private Integer serviceFee;
}
