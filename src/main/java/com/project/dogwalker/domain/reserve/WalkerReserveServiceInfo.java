package com.project.dogwalker.domain.reserve;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;

import com.project.dogwalker.domain.BaseEntity;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "walker_reserve_service",uniqueConstraints={
    @UniqueConstraint(
        name="unique_walker_datetime",
        columnNames={"walker_id", "walker_service_date"}
    )
})
public class WalkerReserveServiceInfo extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "walker_reserve_service_id")
  private Long reserveId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customer_id",nullable = false)
  private User customer;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "walker_id",nullable = false)
  private User walker;

  @Column(name = "walker_service_date",nullable = false)
  private LocalDateTime serviceDateTime;

  @Column(name = "walker_service_time_unit",nullable = false)
  private Integer timeUnit;

  @Builder.Default
  @Column(name = "walker_service_status",nullable = false)
  @Enumerated(EnumType.STRING)
  private WalkerServiceStatus status= WALKER_CHECKING;

  @Column(name = "walker_reserve_service_price")
  private Integer servicePrice;

  public static WalkerReserveServiceInfo of(final ReserveRequest request,final User user,final User walker){
    return WalkerReserveServiceInfo.builder()
        .customer(user)
        .serviceDateTime(request.getServiceDateTime())
        .servicePrice(request.getPrice())
        .timeUnit(request.getTimeUnit())
        .walker(walker)
        .build();
  }

  public void modifyStatus(final WalkerServiceStatus walkerServiceStatus){
    this.status=walkerServiceStatus;
  }

}
