package com.project.dogwalker.domain.user.walker;

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
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "walker_schedule_temporary")
public class WalkerScheduleTemp {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "walker_sc_temp_id")
  private Long walkerScTempId;

  @Column(name = "walker_id",nullable = false)
  private Long walkerId;

  @Column(name = "unavailable_date",nullable = false)
  private LocalDateTime dateTime;

}
