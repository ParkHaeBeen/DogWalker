package com.project.dogwalker.notice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.dogwalker.support.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class NoticeControllerTest extends ControllerTest {

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