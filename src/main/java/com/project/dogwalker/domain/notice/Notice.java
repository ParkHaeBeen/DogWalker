package com.project.dogwalker.domain.notice;

import com.project.dogwalker.domain.BaseEntity;
import com.project.dogwalker.domain.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@AttributeOverride(name = "createdAt", column = @Column(name = "notice_created_at"))
@AttributeOverride(name = "updatedAt", column = @Column(name = "notice_updated_at"))
public class Notice extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notice_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User receiver;

  @Column(name = "notice_type")
  @Enumerated(EnumType.STRING)
  private NoticeType noticeType;

  @Column(name = "notice_path")
  private String path;

  @Column(name = "notice_check_date")
  private LocalDate checkDate;
}
