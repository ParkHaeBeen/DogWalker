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
@Table(name = "walker_schedule")
public class WalkerSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long walkerScId;

  @Column(name = "walker_Id",nullable = false)
  private Long walkerId;

  @Column(name = "unavailable_day",nullable = false)
  private String dayOfWeek;

  @Column(name = "unavailable_time_start",nullable = false)
  private Integer startTime;

  @Column(name = "unavailable_time_end",nullable = false)
  private Integer endTime;
}
