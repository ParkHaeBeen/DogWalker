package com.project.dogwalker.reserve.service;

import static com.project.dogwalker.domain.reserve.PayStatus.ADJUST_DONE;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.CUSTOMER_CANCEL;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_ALREAY;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_REQUEST_NOT_EXIST;

import com.project.dogwalker.aop.distribute.DistributedLock;
import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.member.MemberException;
import com.project.dogwalker.exception.reserve.ReserveException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.dto.NoticeRequest;
import com.project.dogwalker.notice.service.NoticeService;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.dto.ReserveStatusRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReserveServiceImpl implements ReserveService{

  private final WalkerReserveServiceRepository reserveServiceRepository;
  private final PayHistoryRespository payHistoryRespository;
  private final UserRepository userRepository;
  private final NoticeService noticeService;

  /**
   * db에 이미 예약이 되어잇는지 확인
   */
  @Override
  @Transactional(readOnly = true)
  public void isReserved(final ReserveCheckRequest request) {
    existReserve(request.getWalkerId(),request.getServiceDate());
  }


  /**
   * db에 해당 예약있는지 확인
   * user,walker 존재하는지 확인후
   * 예약 및 결제 진행
   * 분산락 이용
   */
  @Override
  @DistributedLock
  public ReserveResponse reserveService(MemberInfo memberInfo , ReserveRequest request) {
    existReserve(request.getWalkerId(),request.getServiceDateTime());
    final User customer = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));

    final User walker = userRepository.findByUserIdAndUserRole(request.getWalkerId() , Role.WALKER)
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));

    final WalkerReserveServiceInfo reserveService = WalkerReserveServiceInfo.of(request , customer , walker);
    final PayHistory payHistory = PayHistory.of(request , customer);

    final PayHistory pay = payHistoryRespository.save(payHistory);
    final WalkerReserveServiceInfo reserve = reserveServiceRepository.save(reserveService);

    Map <String, String > params=new HashMap <>();
    params.put("senderName",walker.getUserName());

    noticeService.send(NoticeRequest.builder()
            .noticeType(NoticeType.RESERVE)
            .receiver(walker)
            .params(params)
            .path("/api/reserve/request/"+reserveService.getReserveId())
        .build());

    return ReserveResponse.builder()
        .payDate(pay.getCreatedAt())
        .price(pay.getPayPrice())
        .serviceDate(reserve.getServiceDateTime())
        .timeUnit(reserve.getTimeUnit())
        .walkerName(walker.getUserName())
        .build();
  }


  private void existReserve(Long walkerId, LocalDateTime serviceDate) {
    if(reserveServiceRepository.findByWalkerUserIdAndServiceDateTime(walkerId , serviceDate).isPresent()){
      throw new ReserveException(RESERVE_ALREAY);
    }
  }

  /**
   * 하루 전날까지 취소 가능
   */
  @Override
  @Transactional
  public ReserveCancel.Response reserveCancel(MemberInfo memberInfo , ReserveCancel.Request request) {
    final User customer = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));

    final WalkerReserveServiceInfo reserveInfo = reserveServiceRepository.findById(request.getReserveId())
        .orElseThrow(() -> new ReserveException(RESERVE_REQUEST_NOT_EXIST));

    if(Duration.between(request.getNow(),reserveInfo.getServiceDateTime()).toDays()<1){
      throw new ReserveException(ErrorCode.RESERVE_CANCEL_UNAVAIL);
    }

    final PayHistory payHistory = payHistoryRespository.findByWalkerReserveInfoReserveId(
        reserveInfo.getReserveId()).orElseThrow(() -> new ReserveException(ErrorCode.NOT_FOUND_PAY_HISTORY));

    reserveInfo.modifyStatus(CUSTOMER_CANCEL);
    payHistory.modifyStatus(ADJUST_DONE);
    return ReserveCancel.Response.builder()
        .serviceDate(reserveInfo.getServiceDateTime())
        .cancelDate(reserveInfo.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional
  public void changeRequestServiceStatus(final MemberInfo memberInfo ,final ReserveStatusRequest request) {
    final User walker = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));

    WalkerReserveServiceInfo serviceInfo = reserveServiceRepository.findByReserveIdAndStatusAndWalkerUserId(
            request.getReserveId() ,request.getStatus() , walker.getUserId())
        .orElseThrow(() -> new ReserveException(RESERVE_REQUEST_NOT_EXIST));

    Map <String, String > params=new HashMap <>();
    params.put("senderName",walker.getUserName());
    params.put("requestType",request.getStatus().toString());
    noticeService.send(NoticeRequest.builder()
        .noticeType(NoticeType.REQUEST_CONFIRM)
        .receiver(serviceInfo.getCustomer())
        .params(params)
        .path(null)
        .build());

    serviceInfo.modifyStatus(WalkerServiceStatus.WALKER_ACCEPT);
  }
}
