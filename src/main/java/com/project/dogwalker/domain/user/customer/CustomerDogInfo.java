package com.project.dogwalker.domain.user.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customer_dog_info")
public class CustomerDogInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "dog_id",nullable = false)
  private Long customerDogId;

  @Column(name = "user_id")
  private Long masterId;

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
