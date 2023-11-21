package com.project.dogwalker.domain.user;

import com.project.dogwalker.member.dto.join.JoinCommonRequest;
import com.project.dogwalker.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "users",
      uniqueConstraints = {@UniqueConstraint(name = "users_email_unique",columnNames = {"user_email"})})
public class User extends BaseEntity {

  @Id
  @GeneratedValue
  private Long userId;

  @Column(name = "user_email",nullable = false)
  private String userEmail;

  @Pattern(regexp = "^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$")
  @Column(name = "user_phoneNumber",nullable = false)
  private String userPhoneNumber;

  @Column(name = "user_lat",nullable = false)
  private Double userLat;

  @Column(name = "user_lnt",nullable = false)
  private Double userLnt;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "user_role",nullable = false)
  private Role userRole=Role.USER;

  @Column(name = "user_name",nullable = false)
  private String userName;

  public static User from(final JoinCommonRequest request){
    return User.builder()
        .userEmail(request.getEmail())
        .userPhoneNumber(request.getPhoneNumber())
        .userLat(request.getLat())
        .userLnt(request.getLnt())
        .userName(request.getName())
        .build();
  }
}
