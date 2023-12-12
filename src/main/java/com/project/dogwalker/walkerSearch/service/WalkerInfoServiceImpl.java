package com.project.dogwalker.walkerSearch.service;

import static com.project.dogwalker.domain.user.Role.WALKER;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;

import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.walker.WalkerScheduleRepository;
import com.project.dogwalker.domain.user.walker.WalkerServicePriceRepository;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.exception.member.MemberNotFoundException;
import com.project.dogwalker.walkerSearch.dto.WalkerInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkerSearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerTimePrice;
import com.project.dogwalker.walkerSearch.dto.WalkerUnAvailDetail;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalkerInfoServiceImpl implements WalkerInfoService {

  private final WalkerSearchRepository walkerSearchRepository;
  private final UserRepository userRepository;
  private final WalkerScheduleRepository walkerScheduleRepository;
  private final WalkerServicePriceRepository walkerServicePriceRepository;

  /**
   * 고객 위치 중심으로 검색 , 추가적으로 이름 겁색 가능
   */
  @Override
  @Transactional(readOnly = true)
  public List <WalkerInfo> getWalkerInfoList(final WalkerInfoSearchCond searchCond) {
    final Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond);

    return walkerDocuments.stream().map(WalkerInfo::of)
        .collect(Collectors.toList());
  }

  /**
   * 서비수 수행자가 안되는 요일과 시간 + 일시적으로 안되는 날찌
   * 서비스 수행자 단위시간당 가격
   */
  @Override
  @Transactional(readOnly = true)
  public WalkerUnAvailDetail getWalkerUnAvailService(Long walkerId) {
    final User walker = userRepository.findByUserIdAndUserRole(walkerId , WALKER)
        .orElseThrow(() -> new MemberNotFoundException(NOT_EXIST_MEMBER));

    final List <WalkerPermUnAvailDate> walkerPermUnAvailDates = userRepository.walkerPermUnVailScheduleFindByWalkerId(
        walker.getUserId());
    final List <WalkerTempUnAvailDate> walkerTempUnAvailDates = userRepository.walkerTempUnAvailFindByWalkerId(
        walker.getUserId());
    final List<WalkerTimePrice> walkerTimePrices=userRepository.walkerTimePrices(walker.getUserId());
    return WalkerUnAvailDetail.builder()
        .walkerName(walker.getUserName())
        .lat(walker.getUserLat())
        .lnt(walker.getUserLnt())
        .permUnAvailDates(walkerPermUnAvailDates)
        .tempUnAvailDates(walkerTempUnAvailDates)
        .timePrices(walkerTimePrices)
        .build();
  }

  /**
   * 해당날짜에 예약이 있는지 전송
   */
  @Override
  @Transactional(readOnly = true)
  public List <WalkerReserveInfo.Response> getReserveDate(final WalkerReserveInfo.Request request) {
    return userRepository.walkerReserveDate(request.getWalkerId(),request.getCheckReserveDate());
  }
}
