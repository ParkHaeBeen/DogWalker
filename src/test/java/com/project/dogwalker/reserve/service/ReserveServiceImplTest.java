package com.project.dogwalker.reserve.service;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_CHECKING;
import static com.project.dogwalker.domain.user.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
import com.project.dogwalker.exception.reserve.ReserveRequestNotExistException;
import com.project.dogwalker.exception.reserve.ReserveUnAvailCancelException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.service.NoticeServiceImpl;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveStatusRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
class ReserveServiceImplTest {

  @Mock
  private WalkerReserveServiceRepository walkerReserveServiceRepository;

  @Mock
  private PayHistoryRespository payHistoryRespository;

  @Mock
  private UserRepository userRepository;

  @MockBean
  private RedissonClient redissonClient;
  @Mock
  private NoticeServiceImpl noticeService;

  @InjectMocks
  private ReserveServiceImpl reserveService;


  @Test
  @DisplayName("예약이 되어 있지 않음")
  void isReserved_success(){
    //given
    ReserveCheckRequest request=ReserveCheckRequest.builder()
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .walkerId(1L)
        .build();

    given(walkerReserveServiceRepository.findByWalkerUserIdAndServiceDateTime(anyLong(),any()))
        .willReturn(Optional.empty());

    //when
    reserveService.isReserved(request);
    //then

  }

  @Test
  @DisplayName("예약이 되어 있어 실패")
  void isReserve_fail(){
    //given
    ReserveCheckRequest request=ReserveCheckRequest.builder()
        .serviceDate(LocalDateTime.of(2023,12,12,12,30))
        .walkerId(1L)
        .build();
    WalkerReserveServiceInfo walkerReserveServiceInfo=WalkerReserveServiceInfo.builder().build();
    given(walkerReserveServiceRepository.findByWalkerUserIdAndServiceDateTime(anyLong(),any()))
        .willReturn(Optional.of(walkerReserveServiceInfo));

    //when
    //then
    Assertions.assertThrows(ReserveAlreadyException.class,()->reserveService.isReserved(request));

  }


  @Test
  @DisplayName("하루 전날까지 취소 가능 - 성공")
  void cancel_success(){
    //given
    User customer= User.builder()
        .userRole(Role.USER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("dis1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("dis1@gmail.com")
        .build();
    PayHistory payHistory=PayHistory.builder()
        .payPrice(10000)
        .customer(customer)
        .payMethod("CARD")
        .payStatus(PayStatus.PAY_REFUND)
        .build();
    WalkerReserveServiceInfo walkerReserveServiceInfo=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,12,15,0))
        .payHistory(payHistory)
        .build();

    ReserveCancel.Request request=ReserveCancel.Request.builder()
        .reserveId(1L)
        .now(LocalDateTime.of(2023,12,10,16,0))
        .build();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(customer.getUserEmail())
        .role(USER)
        .build();

    given(userRepository.findByUserEmailAndUserRole(anyString(),any())).willReturn(Optional.of(customer));
    given(walkerReserveServiceRepository.findById(anyLong())).willReturn(Optional.of(walkerReserveServiceInfo));
    //when
    ReserveCancel.Response response = reserveService.reserveCancel(memberInfo , request);

    //then
    assertThat(response.getServiceDate()).isEqualTo(walkerReserveServiceInfo.getServiceDateTime());
  }

  @Test
  @DisplayName("하루 전날까지 취소 진행 - 해당 예약이 없어서 실패")
  void cancel_fail_notFoundReserve(){
    //given
    User customer= User.builder()
        .userRole(Role.USER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("dis1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("dis1@gmail.com")
        .build();
    WalkerReserveServiceInfo walkerReserveServiceInfo=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,12,15,0))
        .build();

    ReserveCancel.Request request=ReserveCancel.Request.builder()
        .reserveId(1L)
        .now(LocalDateTime.of(2023,12,10,16,0))
        .build();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(customer.getUserEmail())
        .role(USER)
        .build();

    given(userRepository.findByUserEmailAndUserRole(anyString(),any())).willReturn(Optional.of(customer));
    given(walkerReserveServiceRepository.findById(anyLong())).willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(ReserveRequestNotExistException.class,()->reserveService.reserveCancel(memberInfo,request));
  }

  @Test
  @DisplayName("하루 전날까지 취소 진행 - 해당 예약이 예약 취소 가능 기간 지나서 실패")
  void cancel_fail_cancelPeriod(){
    //given
    User customer= User.builder()
        .userRole(Role.USER)
        .userLat(12.0)
        .userLnt(15.0)
        .userName("dis1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("dis1@gmail.com")
        .build();
    WalkerReserveServiceInfo walkerReserveServiceInfo=WalkerReserveServiceInfo.builder()
        .serviceDateTime(LocalDateTime.of(2023,12,12,15,0))
        .build();

    ReserveCancel.Request request=ReserveCancel.Request.builder()
        .reserveId(1L)
        .now(LocalDateTime.of(2023,12,12,20,0))
        .build();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(customer.getUserEmail())
        .role(USER)
        .build();

    given(userRepository.findByUserEmailAndUserRole(anyString(),any())).willReturn(Optional.of(customer));
    given(walkerReserveServiceRepository.findById(anyLong())).willReturn(Optional.of(walkerReserveServiceInfo));

    //when
    //then
    Assertions.assertThrows(
        ReserveUnAvailCancelException.class,()->reserveService.reserveCancel(memberInfo,request));
  }


  @Test
  @DisplayName("서비스 수행자 요청 수락/거부 -성공")
  void changeRequestServiceStatus_success(){
    //given
    User walker= User.builder()
        .userRole(Role.WALKER)
        .userLat(12.0)
        .userLnt(15.0)
        .userId(1L)
        .userName("request1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("request1@gmail.com")
        .build();

    User customer= User.builder()
        .userId(2L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerservice2@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerService2")
        .userRole(Role.USER)
        .build();


    WalkerReserveServiceInfo serviceInfo=WalkerReserveServiceInfo.builder()
        .reserveId(1L)
        .customer(customer)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now())
        .timeUnit(40)
        .status(WALKER_CHECKING)
        .servicePrice(10000)
        .build();

    MemberInfo memberInfo=MemberInfo.builder()
        .email(walker.getUserEmail())
        .role(walker.getUserRole())
        .build();

    ReserveStatusRequest request=ReserveStatusRequest.builder()
        .reserveId(1L)
        .status(WalkerServiceStatus.WALKER_ACCEPT)
        .build();

    //when
    given(userRepository.findByUserEmailAndUserRole(any(),any())).willReturn(Optional.of(walker));
    given(walkerReserveServiceRepository.findByReserveIdAndStatusAndWalkerUserId(anyLong(), any(),anyLong()))
        .willReturn(Optional.of(serviceInfo));

    //then
    reserveService.changeRequestServiceStatus(memberInfo,request);
  }

  @Test
  @DisplayName("서비스 수행자 요청 수락/거부 - 실패 : reserveInfo 존재하지 않음")
  void changeRequestServiceStatus_fail_notFoundReserve(){
    //given
    User walker= User.builder()
        .userRole(Role.WALKER)
        .userLat(12.0)
        .userLnt(15.0)
        .userId(1L)
        .userName("request1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("request1@gmail.com")
        .build();

    User customer= User.builder()
        .userId(2L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerservice2@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerService2")
        .userRole(Role.USER)
        .build();


    MemberInfo memberInfo=MemberInfo.builder()
        .email(walker.getUserEmail())
        .role(walker.getUserRole())
        .build();

    ReserveStatusRequest request=ReserveStatusRequest.builder()
        .reserveId(1L)
        .status(WalkerServiceStatus.WALKER_ACCEPT)
        .build();

    //when
    given(userRepository.findByUserEmailAndUserRole(any(),any())).willReturn(Optional.of(walker));
    given(walkerReserveServiceRepository.findByReserveIdAndStatusAndWalkerUserId(anyLong(), any(),anyLong()))
        .willReturn(Optional.empty());

    //then
    Assertions.assertThrows(ReserveRequestNotExistException.class,()->reserveService.changeRequestServiceStatus(memberInfo,request));
  }

  @Test
  @DisplayName("서비스 수행자 요청 수락/거부 - 실패 : 해당 유저 없음")
  void changeRequestServiceStatus_fail_notFoundUser(){
    //given
    User walker= User.builder()
        .userRole(Role.WALKER)
        .userLat(12.0)
        .userLnt(15.0)
        .userId(1L)
        .userName("request1")
        .userPhoneNumber("010-1234-1234")
        .userEmail("request1@gmail.com")
        .build();

    User customer= User.builder()
        .userId(2L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerservice2@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerService2")
        .userRole(Role.USER)
        .build();


    MemberInfo memberInfo=MemberInfo.builder()
        .email(walker.getUserEmail())
        .role(walker.getUserRole())
        .build();

    ReserveStatusRequest request=ReserveStatusRequest.builder()
        .reserveId(1L)
        .status(WalkerServiceStatus.WALKER_ACCEPT)
        .build();

    //when
    given(userRepository.findByUserEmailAndUserRole(any(),any())).willReturn(Optional.empty());


    //then
    Assertions.assertThrows(
        MemberNotFoundException.class,()->reserveService.changeRequestServiceStatus(memberInfo,request));
  }
}