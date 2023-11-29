package com.project.dogwalker.domain.reserve;

import com.project.dogwalker.domain.BaseEntity;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "walker_reserve_service")
@AttributeOverride(name = "createdAt", column = @Column(name = "walker_reserve_service_created_at"))
@AttributeOverride(name = "updatedAt", column = @Column(name = "walker_reserve_service_updated_at"))
public class WalkerReserveService extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "walker_reserve_service_id")
  private Long reserveId;

  @ManyToOne
  @Column(name = "customer_id",nullable = false)
  private User customer;

  @ManyToOne
  @Column(name = "walker_id",nullable = false)
  private User walker;

  @Column(name = "walker_service_implement_date",nullable = false)
  private LocalDateTime serviceDate;

  @Column(name = "walker_service_implement_unit",nullable = false)
  private Integer timeUnit;

  //고객 취소 = CC, 서비스 수행자 예약 수락 여부 = WY(수락), WN(거부),WP(진행중)
  @Builder.Default
  @Column(name = "walker_service_status",nullable = false)
  private String status="WP";

  @Column(name = "walker_reserve_service_price")
  private Integer servicePrice;

  @OneToOne(mappedBy = "reserveService",fetch = FetchType.LAZY)
  private PayHistory payHistory;

  public static WalkerReserveService of(final ReserveRequest request,final User user,final User walker){
    return WalkerReserveService.builder()
        .customer(user)
        .serviceDate(request.getServiceDate())
        .servicePrice(request.getPrice())
        .timeUnit(request.getTimeUnit())
        .walker(walker)
        .build();
  }
}
