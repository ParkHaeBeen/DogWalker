package com.project.dogwalker.notice.service;

import com.project.dogwalker.domain.notice.EmitterRepository;
import com.project.dogwalker.domain.notice.Notice;
import com.project.dogwalker.domain.notice.NoticeRepository;
import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.notice.NoticeNotFoundException;
import com.project.dogwalker.exception.notice.SseException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.dto.NoticeRequest;
import com.project.dogwalker.notice.dto.NoticeResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

  private final EmitterRepository emitterRepository;
  private final NoticeRepository noticeRepository;
  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
  @Override
  public SseEmitter addEmitter(final MemberInfo memberInfo ,final String lastEventId) {
    final String id = memberInfo.getEmail()+"-"+System.currentTimeMillis();
    SseEmitter emitter=emitterRepository.save(id,new SseEmitter(DEFAULT_TIMEOUT));

    emitter.onCompletion(() -> emitterRepository.deleteByEmail(id));
    emitter.onTimeout(() -> emitterRepository.deleteByEmail(id));

    sendToClient(emitter, id, "Create EventStream : email =" + memberInfo.getEmail());

    if (!lastEventId.isEmpty()) {
      Map<String, Object> events = emitterRepository.findAllEventCacheStartWithEmail(memberInfo.getEmail());
      events.entrySet().stream()
          .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
          .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }

    return emitter;
  }
  private void sendToClient(SseEmitter emitter, String id, Object data) {
    try {
      emitter.send(SseEmitter.event()
          .id(id)
          .name("sse")
          .data(data));
    } catch (IOException exception) {
        emitterRepository.deleteByEmail(id);
        throw new SseException(ErrorCode.SSE_ERROR);
      }
  }

  @Override
  public void send(final NoticeRequest noticeRequest) {
    Notice notice=noticeRepository.save(createNotice(noticeRequest));
    String id=noticeRequest.getReceiver().getUserEmail();

    Map <String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithByEmail(id);
    sseEmitters.forEach(
        (key, emitter) -> {
          emitterRepository.saveEventCache(key, notice);
          sendToClient(emitter, key, NoticeResponse.builder()
              .sendTime(LocalDateTime.now())
              .path(noticeRequest.getPath())
              .message(createMessage(noticeRequest))
              .build());
        }
    );
  }

  private Notice createNotice(final NoticeRequest noticeRequest){
    return Notice.builder()
        .noticeType(noticeRequest.getNoticeType())
        .path(noticeRequest.getPath())
        .receiver(noticeRequest.getReceiver())
        .build();
  }

  private String createMessage(NoticeRequest request){
    final Map <String, String> params = request.getParams();
    String senderName = params.get("senderName");

    if(request.getNoticeType()== NoticeType.SERVICE){
      return senderName+"님 산책 종료 5분전입니다. 대기해주세요.";
    }

    if(request.getNoticeType()==NoticeType.RESERVE){
      return senderName+"님이 예약을 요청했습니다. 10분안에 확인해주세요.";
    }

    if(request.getNoticeType()==NoticeType.REQUEST_CONFIRM){
      return senderName+"님이 예약을 "+params.get("requestType")+"을 하셨습니다.";
    }

    return null;
  }

  @Override
  @Transactional
  public void readNotification(Long id) {
    Notice notice = noticeRepository.findById(id)
        .orElseThrow(() -> new NoticeNotFoundException(ErrorCode.NOT_FOUND_NOTICE));
    notice.setCheckDate(LocalDateTime.now());
  }
}

