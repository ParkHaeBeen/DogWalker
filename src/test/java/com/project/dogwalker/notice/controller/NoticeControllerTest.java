package com.project.dogwalker.notice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dogwalker.common.config.WebConfig;
import com.project.dogwalker.member.token.JwtTokenProvider;
import com.project.dogwalker.notice.service.NoticeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(NoticeController.class)
@Import(WebConfig.class)
class NoticeControllerTest {

  @MockBean
  private NoticeServiceImpl noticeService;

  @InjectMocks
  private NoticeController noticeController;
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("sse 연결")
  void subscribeSse() throws Exception {
    //when
    ResultActions resultActions = mockMvc.perform(
        get("/api/sse/connect")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .header("Last_Event_ID" , "1_12345679"));

    //then
    resultActions.andExpect(status().isOk());
  }

  @Test
  @DisplayName("안 읽은 알림 클릭시 읾음 상태로 변경")
  void readNotification() throws Exception {
    //given
    //when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/sse/read/{id}",1)
    );

    //then
    resultActions.andExpect(status().isNoContent());
  }
}