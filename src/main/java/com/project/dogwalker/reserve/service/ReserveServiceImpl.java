package com.project.dogwalker.reserve.service;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_ALREAY;

import com.project.dogwalker.aop.distribute.DistributedLock;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayHistoryRespository;
import com.project.dogwalker.domain.reserve.PayStatus;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.exception.member.MemberNotFoundException;
import com.project.dogwalker.exception.reserve.ReserveAlreadyException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
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
   */
  @Override
  @DistributedLock
  public ReserveResponse reserveService(MemberInfo memberInfo , ReserveRequest request) {
    log.info("reserve service start");
    existReserve(request.getWalkerId(),request.getServiceDateTime());
    final User customer = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final User walker = userRepository.findByUserIdAndUserRole(request.getWalkerId() , Role.WALKER)
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final WalkerReserveServiceInfo reserveService = WalkerReserveServiceInfo.of(request , customer , walker);
    final PayHistory payHistory = PayHistory.of(request , reserveService);

    final PayHistory pay = payHistoryRespository.save(payHistory);
    final WalkerReserveServiceInfo reserve = reserveServiceRepository.save(reserveService);
    log.info("reserve service end");
    return ReserveResponse.builder()
        .payDate(pay.getCreatedAt())
        .price(pay.getPayPrice())
        .serviceDate(reserve.getServiceDateTime())
        .timeUnit(reserve.getTimeUnit())
        .walkerName(walker.getUserName())
        .build();
  }


  /**
   * 점주에게 신규예약에 대해 10분후 수락/거절 안하면 자동 거절
   */
  @Override
  public void changeReserveStatus(){
    reserveServiceRepository.findAllByCreatedAtBeforeAndStatus(
        LocalDateTime.now().minusMinutes(10) ,
        WALKER_CHECKING).stream()
        .map(service ->
                    {service.setStatus(WalkerServiceStatus.WALKER_REFUSE);
                      service.getPayHistory().setPayStatus(PayStatus.PAY_REFUND);
                      return service;})
        .collect(Collectors.toList());

  }

  private void existReserve(Long walkerId, LocalDateTime serviceDate) {
    log.info("reserve exist start");

    if(reserveServiceRepository.findByWalkerUserIdAndServiceDateTime(walkerId , serviceDate).isPresent()){
      log.info("reserve exist true");
      throw new ReserveAlreadyException(RESERVE_ALREAY);
    }
    log.info("reserve exist end");
  }

}
