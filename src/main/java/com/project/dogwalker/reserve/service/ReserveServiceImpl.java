package com.project.dogwalker.reserve.service;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.CUSTOMER_CANCEL;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_ALREAY;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_REQUEST_NOT_EXIST;

import com.project.dogwalker.aop.distribute.DistributedLock;
import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.PayStatus;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.member.MemberNotFoundException;
import com.project.dogwalker.exception.reserve.ReserveAlreadyException;
import com.project.dogwalker.exception.reserve.ReserveRequestNotExistException;
import com.project.dogwalker.exception.reserve.ReserveUnAvailCancelException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.dto.NoticeRequest;
import com.project.dogwalker.notice.service.NoticeService;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import java.time.Duration;
import java.time.LocalDateTime;
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
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final User walker = userRepository.findByUserIdAndUserRole(request.getWalkerId() , Role.WALKER)
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final WalkerReserveServiceInfo reserveService = WalkerReserveServiceInfo.of(request , customer , walker);
    final PayHistory payHistory = PayHistory.of(request , customer);

    final PayHistory pay = payHistoryRespository.save(payHistory);
    final WalkerReserveServiceInfo reserve = reserveServiceRepository.save(reserveService);

    noticeService.send(NoticeRequest.builder()
            .noticeType(NoticeType.RESERVE)
            .receiver(walker)
            .senderName(customer.getUserName())
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
      throw new ReserveAlreadyException(RESERVE_ALREAY);
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
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    WalkerReserveServiceInfo reserveInfo = reserveServiceRepository.findById(request.getReserveId())
        .orElseThrow(() -> new ReserveRequestNotExistException(RESERVE_REQUEST_NOT_EXIST));

    if(Duration.between(request.getNow(),reserveInfo.getServiceDateTime()).toDays()<1){
      throw new ReserveUnAvailCancelException(ErrorCode.RESERVE_CANCEL_UNAVAIL);
    }

    reserveInfo.setStatus(CUSTOMER_CANCEL);
    reserveInfo.getPayHistory().setPayStatus(PayStatus.PAY_REFUND);
    return ReserveCancel.Response.builder()
        .serviceDate(reserveInfo.getServiceDateTime())
        .cancelDate(reserveInfo.getUpdatedAt())
        .build();
  }

  @Override
  @Transactional
  public void changeRequestServiceStatus(final MemberInfo memberInfo ,final Long reserveId) {
    final User walker = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    WalkerReserveServiceInfo serviceInfo = reserveServiceRepository.findByReserveIdAndStatusAndWalkerUserId(
            reserveId , WALKER_CHECKING , walker.getUserId())
        .orElseThrow(() -> new ReserveRequestNotExistException(RESERVE_REQUEST_NOT_EXIST));
    serviceInfo.setStatus(WalkerServiceStatus.WALKER_ACCEPT);
  }
}
