package com.project.dogwalker.notice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.project.dogwalker.domain.notice.EmitterRepositoryImpl;
import com.project.dogwalker.domain.notice.Notice;
import com.project.dogwalker.domain.notice.NoticeRepository;
import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.exception.notice.NoticeException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.dto.NoticeRequest;
import com.project.dogwalker.support.fixture.MemberInfoFixture;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@ExtendWith (MockitoExtension.class)
@ActiveProfiles (profiles = "local")
class NoticeServiceImplTest {

  @Mock
  private EmitterRepositoryImpl emitterRepository;

  @Mock
  private NoticeRepository noticeRepository;

  @InjectMocks
  private NoticeServiceImpl noticeService;

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

  @Test
  @DisplayName("sse 연결 성공 - last eventId 없음")
  void addEmitter_success_no_last_event_Id() {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_WALKER.생성();
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

    given(emitterRepository.save(anyString(),any())).willReturn(sseEmitter);
    //when
    SseEmitter sseEmitterResponse = noticeService.addEmitter(memberInfo , "");

    //then
    Assertions.assertThat(sseEmitterResponse.getTimeout()).isEqualTo(DEFAULT_TIMEOUT);
  }

  @Test
  @DisplayName("sse 연결 성공 - last eventId ")
  void addEmitter_success_last_event_Id() {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_WALKER.생성();
    SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

    given(emitterRepository.save(anyString(),any())).willReturn(sseEmitter);
    //when
    SseEmitter sseEmitterResponse = noticeService.addEmitter(memberInfo , "lastEventId");

    //then
    Assertions.assertThat(sseEmitterResponse.getTimeout()).isEqualTo(DEFAULT_TIMEOUT);
  }



  @Test
  @DisplayName("sse send 성공 - reserve")
  void send_success_reserve () {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_WALKER.생성();

    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(memberInfo.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(memberInfo.getRole())
        .build();

    Map<String, String > params = new HashMap <>();
    params.put("senderName", user.getUserName());

    NoticeRequest noticeRequest = NoticeRequest.builder()
        .noticeType(NoticeType.RESERVE)
        .path("/reserve")
        .receiver(user)
        .params(params)
        .build();

    SseEmitter emitter1 = new SseEmitter();
    SseEmitter emitter2 = new SseEmitter();
    Map<String, SseEmitter> sseEmitters = new HashMap <>();
    sseEmitters.put(memberInfo.getEmail(), emitter1);
    sseEmitters.put("test2@example.com", emitter2);

    given(emitterRepository.findAllStartWithByEmail(anyString())).willReturn(sseEmitters);

    //when
    //then
    noticeService.send(noticeRequest);

  }

  @Test
  @DisplayName("sse send 성공 - service")
  void send_success_service () {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_USER.생성();

    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(memberInfo.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(memberInfo.getRole())
        .build();

    Map<String, String > params = new HashMap <>();
    params.put("senderName", user.getUserName());

    NoticeRequest noticeRequest = NoticeRequest.builder()
        .noticeType(NoticeType.SERVICE)
        .path("/reserve")
        .receiver(user)
        .params(params)
        .build();

    SseEmitter emitter1 = new SseEmitter();
    SseEmitter emitter2 = new SseEmitter();
    Map<String, SseEmitter> sseEmitters = new HashMap <>();
    sseEmitters.put(memberInfo.getEmail(), emitter1);
    sseEmitters.put("test2@example.com", emitter2);

    given(emitterRepository.findAllStartWithByEmail(anyString())).willReturn(sseEmitters);

    //when
    //then
    noticeService.send(noticeRequest);

  }

  @Test
  @DisplayName("sse send 성공 - request confirm")
  void send_success_request_confirm () {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_USER.생성();

    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(memberInfo.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(memberInfo.getRole())
        .build();

    Map<String, String > params = new HashMap <>();
    params.put("senderName", user.getUserName());

    NoticeRequest noticeRequest = NoticeRequest.builder()
        .noticeType(NoticeType.REQUEST_CONFIRM)
        .path("/reserve")
        .receiver(user)
        .params(params)
        .build();

    SseEmitter emitter1 = new SseEmitter();
    SseEmitter emitter2 = new SseEmitter();
    Map<String, SseEmitter> sseEmitters = new HashMap <>();
    sseEmitters.put(memberInfo.getEmail(), emitter1);
    sseEmitters.put("test2@example.com", emitter2);

    given(emitterRepository.findAllStartWithByEmail(anyString())).willReturn(sseEmitters);

    //when
    //then
    noticeService.send(noticeRequest);

  }
  @Test
  @DisplayName("sse send  - 타입이 없음 null")
  void send_success_null() {
    //given
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_USER.생성();

    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(memberInfo.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(memberInfo.getRole())
        .build();

    Map<String, String > params = new HashMap <>();
    params.put("senderName", user.getUserName());

    NoticeRequest noticeRequest = NoticeRequest.builder()
        .noticeType(null)
        .path("/reserve")
        .receiver(user)
        .params(params)
        .build();

    SseEmitter emitter1 = new SseEmitter();
    SseEmitter emitter2 = new SseEmitter();
    Map<String, SseEmitter> sseEmitters = new HashMap <>();
    sseEmitters.put(memberInfo.getEmail(), emitter1);
    sseEmitters.put("test2@example.com", emitter2);

    given(emitterRepository.findAllStartWithByEmail(anyString())).willReturn(sseEmitters);

    //when
    //then
    noticeService.send(noticeRequest);

  }


  @Test
  @DisplayName("알림 읽음 성공")
  void readNotification_success () {
    MemberInfo memberInfo = MemberInfoFixture.MEMBERINFO_WALKER.생성();

    User user= User.builder()
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail(memberInfo.getEmail())
        .userPhoneNumber("010-1234-1234")
        .userName("batchuser1")
        .userRole(memberInfo.getRole())
        .build();
    Notice notice = Notice.builder()
        .receiver(user)
        .noticeType(NoticeType.RESERVE)
        .path("/reserve")
        .build();
    //given
    given(noticeRepository.findById(anyLong())).willReturn(Optional.of(notice));

    //when
    //then
    noticeService.readNotification(1L);
  }

  @Test
  @DisplayName("알림 읽음 실패- 찾을 수 없음")
  void readNotification_fail() {
    //given
    given(noticeRepository.findById(anyLong())).willReturn(Optional.empty());

    //when
    //then
    assertThrows(NoticeException.class, () -> noticeService.readNotification(1L));
  }
}