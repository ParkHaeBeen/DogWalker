package com.project.dogwalker.domain.reserve;

import static com.project.dogwalker.domain.reserve.PayStatus.PAY_DONE;

import com.project.dogwalker.domain.BaseEntity;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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


  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User customer;


  @Column(name = "pay_price")
  private int payPrice;


  @Builder.Default
  @Column(name = "pay_status",nullable = false)
  @Enumerated(EnumType.STRING)
  private PayStatus payStatus= PAY_DONE;

  @Column(name = "pay_method",nullable = false)
  private String payMethod;


  public static PayHistory of(final ReserveRequest request,final User customer){
    return PayHistory.builder()
        .customer(customer)
        .payMethod(request.getPayMethod())
        .payPrice(request.getPrice())
        .build();
  }
}
