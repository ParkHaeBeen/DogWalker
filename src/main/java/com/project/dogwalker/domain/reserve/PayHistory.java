package com.project.dogwalker.domain.reserve;

import static com.project.dogwalker.domain.reserve.PayStatus.*;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "pay_history")
@AttributeOverride(name = "createdAt", column = @Column(name = "pay_created_at"))
@AttributeOverride(name = "updatedAt", column = @Column(name = "pay_updated_at"))
public class PayHistory extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "pay_history_id")
  private Long payId;


  @ManyToOne
  @Column(name = "user_id",nullable = false)
  private User customer;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "walker_reserve_service_id")
  @Column(name = "walker_reserve_service_id",nullable = false)
  private WalkerReserveServiceInfo reserveService;

  @Column(name = "pay_price")
  private int payPrice;

  //결제완료 : PY, 환불 : PR
  @Builder.Default
  @Column(name = "pay_status",nullable = false)
  private PayStatus payStatus= PAY_DONE;

  @Column(name = "pay_method",nullable = false)
  private String payMethod;

  public static PayHistory of(final ReserveRequest request,final WalkerReserveServiceInfo service){
    return PayHistory.builder()
        .customer(service.getCustomer())
        .payMethod(request.getPayMethod())
        .reserveService(service)
        .payPrice(request.getPrice())
        .build();
  }
}
