package com.project.dogwalker.walkerservice.service;

import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_ACCEPT;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;
import static com.project.dogwalker.exception.ErrorCode.RESERVE_REQUEST_NOT_EXIST;

import com.project.dogwalker.domain.notice.NoticeType;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceInfo;
import com.project.dogwalker.domain.reserve.WalkerReserveServiceRepository;
import com.project.dogwalker.domain.reserve.WalkerServiceStatus;
import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.walkerservice.WalkerServiceRoute;
import com.project.dogwalker.domain.walkerservice.WalkerServiceRouteRepository;
import com.project.dogwalker.exception.ErrorCode;
import com.project.dogwalker.exception.member.MemberNotFoundException;
import com.project.dogwalker.exception.reserve.ReserveDateNotMatch;
import com.project.dogwalker.exception.reserve.ReserveRequestNotExistException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.common.service.RedisService;
import com.project.dogwalker.notice.dto.NoticeRequest;
import com.project.dogwalker.notice.service.NoticeService;
import com.project.dogwalker.walkerservice.dto.RealTimeLocation;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalkerServiceImpl implements WalkerService{

  private final UserRepository userRepository;
  private final WalkerReserveServiceRepository reserveRepository;
  private final RedisService redisService;
  private final WalkerServiceRouteRepository routeRepository;
  private final NoticeService noticeService;
  private final String startServicePrefix="start-";
  private final String proceedServicePrefix="proceed-";

  /**
   * 해당 워커가 있는 지확인
   * 해당 예약있는지 확인(+해당 워커가 수행하는게 맞는지)
   * 해당 예약 수행날짜가 현재 날짜인지 확인
   */
  @Override
  @Transactional(readOnly = true)
  public void checkService(final MemberInfo memberInfo ,final ServiceCheckRequest request) {
    final WalkerReserveServiceInfo serviceInfo = validationWalkerAndReserve(
        memberInfo , request.getReserveId());

    if(!serviceInfo.getServiceDateTime().toLocalDate().equals(request.getNowDate().toLocalDate())){
      throw new ReserveDateNotMatch(ErrorCode.RESERVE_DATE_NOT_MATCH);
    }
    startService(serviceInfo.getReserveId() , serviceInfo.getTimeUnit());
  }

  /**
   * 예약 유효성 확인후 redis에 서비스 수행시작되었다고 저장
   */
  @Override
  @Transactional
  public void startService(final Long reserveId,final int timeUnit) {
    redisService.setData(startServicePrefix+reserveId,"ON",timeUnit);
  }

  /**
   * 산책서비스가 시작되었는지 확인
   */
  @Override
  @Transactional(readOnly = true)
  public boolean isStartedService(final Long reserveId) {
    return redisService.getStartData(startServicePrefix+reserveId);
  }

  /**
   * 실시간 경로 Redis list로 저장
   */
  @Override
  @Transactional
  public void saveRealTimeLocation(final RealTimeLocation location) {
    final Coordinate coordinate=new Coordinate(location.getLat(),location.getLnt());
    redisService.addToList(proceedServicePrefix+location.getReserveId(),coordinate);
  }

  @Override
  public void noticeCustomer(final Long reserveId) {
    final WalkerReserveServiceInfo serviceInfo = reserveRepository.findById(reserveId)
        .orElseThrow(() -> new ReserveRequestNotExistException(RESERVE_REQUEST_NOT_EXIST));
    noticeService.send(NoticeRequest.builder()
            .path(null)
            .noticeType(NoticeType.SERVICE)
            .senderName(serviceInfo.getWalker().getUserName())
            .receiver(serviceInfo.getCustomer())
        .build());
  }

  /**
   * 서비스 종료후 redis에 저장한 경로들 db로 저장
   */
  @Override
  @Transactional
  public ServiceEndResponse saveServiceRoute(final MemberInfo memberInfo ,final ServiceEndRequest request) {
    final WalkerReserveServiceInfo serviceInfo = validationWalkerAndReserve(memberInfo ,
        request.getReserveId());
    final List<Coordinate> routes = redisService.getList(proceedServicePrefix+request.getReserveId());

    GeometryFactory geometryFactory=new GeometryFactory();
    final LineString routeLine = geometryFactory.createLineString(routes.toArray(new Coordinate[0]));
    final WalkerServiceRoute walkerServiceRoute = routeRepository.save(WalkerServiceRoute.builder()
        .routes(routeLine)
        .reserveInfo(serviceInfo)
        .build());

    redisService.deleteRedisData(proceedServicePrefix+request.getReserveId());
    serviceInfo.setStatus(WalkerServiceStatus.FINISH);

    return ServiceEndResponse.builder()
        .routeId(walkerServiceRoute.getRouteId())
        .endTime(walkerServiceRoute.getCreatedAt())
        .build();
  }

  private WalkerReserveServiceInfo validationWalkerAndReserve(final MemberInfo memberInfo ,
      final Long reserveId) {
    final User walker = userRepository.findByUserEmailAndUserRole(memberInfo.getEmail() ,
            memberInfo.getRole())
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final WalkerReserveServiceInfo serviceInfo = reserveRepository.findByReserveIdAndStatusAndWalkerUserId(
            reserveId , WALKER_ACCEPT,walker.getUserId())
        .orElseThrow(() -> new ReserveRequestNotExistException(RESERVE_REQUEST_NOT_EXIST));
    return serviceInfo;
  }
}
