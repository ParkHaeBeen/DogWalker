package com.project.dogwalker.notice.dto;

import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.domain.user.User;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeRequest {

  private User receiver;
  private NoticeType noticeType;
  private String path;
  private Map <String,String> params;
}
