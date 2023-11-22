package com.project.dogwalker.domain.user.customer;

import com.project.dogwalker.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "customer_dog_info")
public class CustomerDogInfo {

  @Id
  @GeneratedValue
  @Column(name = "dog_Id",nullable = false)
  private Long customerDogId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dogInfo")
  private User dogMaster;

  @Column(name = "dog_img_url",nullable = false)
  private String dogImgUrl;

  @Column(name = "dog_birth_date",nullable = false)
  private LocalDateTime dogBirth;

  @Column(name = "dog_name",nullable = false)
  private String dogName;

  @Column(name = "dog_type",nullable = false)
  private String dogType;

  @Column(name = "dog_description",nullable = false)
  private String dogDescription;


}
