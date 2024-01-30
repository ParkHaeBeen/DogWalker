package com.project.dogwalker.walkersearch.service;

import static com.project.dogwalker.domain.user.Role.WALKER;
import static com.project.dogwalker.exception.ErrorCode.NOT_EXIST_MEMBER;

import com.project.dogwalker.domain.user.User;
import com.project.dogwalker.domain.user.UserRepository;
import com.project.dogwalker.domain.user.walker.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.walker.elastic.WalkerSearchRepository;
import com.project.dogwalker.exception.member.MemberException;
import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkersearch.dto.response.WalkerInfoResponse;
import com.project.dogwalker.walkersearch.dto.request.WalkerInfoRequest;
import com.project.dogwalker.walkersearch.dto.response.WalkerPermUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.response.WalkerTempUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerTimePriceResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerUnAvailDetailResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalkerInfoServiceImpl implements WalkerInfoService {

  private final WalkerSearchRepository walkerSearchRepository;
  private final UserRepository userRepository;

  /**
   * 고객 위치 중심으로 검색 , 추가적으로 이름 겁색 가능
   */
  @Override
  @Transactional(readOnly = true)
  public List <WalkerInfoResponse> getWalkerInfoList(final MemberInfo info ,final WalkerInfoRequest searchCond,
      final Pageable pageable) {
    final User user = userRepository.findByUserEmailAndUserRole(info.getEmail() , info.getRole())
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
    if(searchCond.getLat() == null || searchCond.getLnt() == null){
      searchCond.setLat(user.getUserLat());
      searchCond.setLnt(user.getUserLnt());
    }
    final Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond,pageable);

    return walkerDocuments.stream().map(WalkerInfoResponse::of)
        .collect(Collectors.toList());
  }

  /**
   * 서비수 수행자가 안되는 요일과 시간 + 일시적으로 안되는 날찌
   * 서비스 수행자 단위시간당 가격
   */
  @Override
  @Transactional(readOnly = true)
  public WalkerUnAvailDetailResponse getWalkerUnAvailService(final Long walkerId) {
    final User walker = userRepository.findByUserIdAndUserRole(walkerId , WALKER)
        .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));

    final List <WalkerPermUnAvailDateResponse> walkerPermUnAvailDateResponses = userRepository.walkerPermUnVailScheduleFindByWalkerId(
        walker.getUserId());
    final List <WalkerTempUnAvailDateResponse> walkerTempUnAvailDateResponses = userRepository.walkerTempUnAvailFindByWalkerId(
        walker.getUserId());
    final List<WalkerTimePriceResponse> walkerTimePriceResponses =userRepository.walkerTimePrices(walker.getUserId());
    return WalkerUnAvailDetailResponse.builder()
        .walkerName(walker.getUserName())
        .lat(walker.getUserLat())
        .lnt(walker.getUserLnt())
        .permUnAvailDates(walkerPermUnAvailDateResponses)
        .tempUnAvailDates(walkerTempUnAvailDateResponses)
        .timePrices(walkerTimePriceResponses)
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
