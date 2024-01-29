package com.project.dogwalker.walkerservice.service;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_ACCEPT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.project.dogwalker.common.service.redis.RedisService;
import com.project.dogwalker.domain.reserve.PayHistory;
import com.project.dogwalker.domain.reserve.PayStatus;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.user.Role;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.walkerservice.WalkerServiceRoute;
import com.project.dogwalker.domain.walkerservice.WalkerServiceRouteRepository;
import com.project.dogwalker.exception.reserve.ReserveDateNotMatch;
import com.project.dogwalker.exception.reserve.ReserveRequestNotExistException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.notice.service.NoticeService;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
@MockitoSettings(strictness = Strictness.LENIENT)
class WalkerServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private WalkerReserveServiceRepository reserveRepository;

  @Mock
  private RedisService redisService;

  @Mock
  private WalkerServiceRouteRepository serviceRouteRepository;

  @Mock
  private NoticeService noticeService;

  @InjectMocks
  private WalkerServiceImpl walkerService;

  void validationWalkerAndReserve(){
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerservice@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerService1")
        .userRole(Role.USER)
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

    PayHistory payHistory=PayHistory.builder()
        .payPrice(10000)
        .payId(1L)
        .payMethod("CARD")
        .payStatus(PayStatus.PAY_DONE)
        .customer(customer)
        .build();

    WalkerReserveServiceInfo serviceInfo=WalkerReserveServiceInfo.builder()
        .reserveId(1L)
        .customer(customer)
        .walker(walker)
        .serviceDateTime(LocalDateTime.now())
        .timeUnit(40)
        .status(WALKER_ACCEPT)
        .servicePrice(10000)
        .build();
    given(userRepository.findByUserEmailAndUserRole(anyString(),any())).willReturn(
        Optional.ofNullable(walker));
    given(reserveRepository.findByReserveIdAndStatusAndWalkerUserId(anyLong(), any() ,anyLong()))
        .willReturn(Optional.ofNullable(serviceInfo));
  }

  @Test
  @DisplayName("예약 수행 날짜 확인 : 현재 날짜 맞음")
  void checkService_success() {
    //given
    MemberInfo info=MemberInfo.builder()
        .email("walkerservice@gmail.com")
        .role(Role.WALKER)
        .build();
    ServiceCheckRequest request=ServiceCheckRequest.builder()
        .nowDate(LocalDateTime.now().minusMinutes(10))
        .reserveId(1L)
        .build();

    //when
    validationWalkerAndReserve();

    //then
   walkerService.checkService(info,request);

  }
  @Test
  @DisplayName("예약 수행 날짜 확인 : 현재 날짜 안맞음 - 실패")
  void checkService_fail() {
    //given
    MemberInfo info=MemberInfo.builder()
        .email("walkerservice@gmail.com")
        .role(Role.WALKER)
        .build();
    ServiceCheckRequest request=ServiceCheckRequest.builder()
        .nowDate(LocalDateTime.now().minusDays(10))
        .reserveId(1L)
        .build();

    //when
    validationWalkerAndReserve();

    //then
    Assertions.assertThrows(ReserveDateNotMatch.class,()-> walkerService.checkService(info,request));
  }

  @Test
  @DisplayName("고객에게 서비스 종료 5분전 알림 - 성공")
  void noticeCustomer_success(){
    //given
    User walker= User.builder()
        .userId(1L)
        .userLat(12.0)
        .userLnt(3.0)
        .userEmail("walkerservice@gmail.com")
        .userPhoneNumber("010-1234-1234")
        .userName("walkerService1")
        .userRole(Role.USER)
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
        .walker(walker)
        .customer(customer)
        .serviceDateTime(LocalDateTime.now())
        .timeUnit(40)
        .status(WALKER_ACCEPT)
        .servicePrice(10000)
        .build();

    given(reserveRepository.findById(anyLong()))
        .willReturn(Optional.of(serviceInfo));
    //when
    //then
    walkerService.noticeCustomer(1L);
  }

  @Test
  @DisplayName("고객에게 서비스 종료 5분전 알림 - 실패 : 해당 예약이 존재하지 않아")
  void noticeCustomer_fail_notReserveFound(){
    //given
    given(reserveRepository.findById(anyLong()))
        .willReturn(Optional.empty());

    //when
    //then
    Assertions.assertThrows(ReserveRequestNotExistException.class,()->walkerService.noticeCustomer(1L));

  }
  @Test
  @DisplayName("서비스 종료후 redis에 저장한 경로들 db 저장 성공")
  void saveServiceRoute() {
    //given
    validationWalkerAndReserve();

    List <Coordinate> coordinateList=new ArrayList <>();
    Coordinate c1=new Coordinate(12.0,12.0);
    Coordinate c2=new Coordinate(12.0002,12.0);
    Coordinate c3=new Coordinate(12.0003,12.0002);
    coordinateList.add(c1);
    coordinateList.add(c2);
    coordinateList.add(c3);

    GeometryFactory geometryFactory=new GeometryFactory();
    LineString routeLine=geometryFactory.createLineString(coordinateList.toArray(new Coordinate[0]));

    WalkerServiceRoute routes=WalkerServiceRoute.builder()
        .routes(routeLine)
        .createdAt(LocalDateTime.of(2023,12,13,0,12))
        .reserveInfo(new WalkerReserveServiceInfo())
        .build();

    MemberInfo info=MemberInfo.builder()
        .email("walkerservice@gmail.com")
        .role(Role.WALKER)
        .build();
    ServiceEndRequest request=ServiceEndRequest.builder()
        .reserveId(1L)
        .build();

    given(redisService.getList(anyString())).willReturn(coordinateList);
    given(serviceRouteRepository.save(any())).willReturn(routes);

    //when
    ServiceEndResponse serviceEndResponse = walkerService.saveServiceRoute(info , request);

    //then
    assertThat(serviceEndResponse.getEndTime()).isEqualTo(LocalDateTime.of(2023,12,13,0,12));
  }
}