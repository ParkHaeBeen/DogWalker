package com.project.dogwalker.domain.user.walker;

import static com.project.dogwalker.domain.reserve.QWalkerReserveServiceInfo.walkerReserveServiceInfo;
import static com.project.dogwalker.domain.reserve.WalkerServiceStatus.WALKER_ACCEPT;
import static com.project.dogwalker.domain.user.QUser.user;
import static com.project.dogwalker.domain.user.walker.QWalkerSchedule.walkerSchedule;
import static com.project.dogwalker.domain.user.walker.QWalkerScheduleTemp.walkerScheduleTemp;
import static com.project.dogwalker.domain.user.walker.QWalkerServicePrice.walkerServicePrice;

import com.project.dogwalker.walkerSearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerTimePrice;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WalkerInfoRepositoryImpl implements WalkerInfoRepository{

  private final JPAQueryFactory queryFactory;

  /**
   * 상시 서비스 불가능한 요일
   */
  @Override
  public List<WalkerPermUnAvailDate> walkerPermUnVailScheduleFindByWalkerId(final Long walkerId){
    return queryFactory
        .select(Projections.constructor(WalkerPermUnAvailDate.class,
                walkerSchedule.dayOfWeek,
                walkerSchedule.startTime,
                walkerSchedule.endTime
        ))
        .from(user)
        .leftJoin(walkerSchedule).on(walkerSchedule.walkerId.eq(user.userId))
        .where(
            user.userId.eq(walkerId)
        ).fetch();
  }

  /**
   * 일시적으로 불가능한 날짜 가져오기
   */
  @Override
  public List<WalkerTempUnAvailDate> walkerTempUnAvailFindByWalkerId(final Long walkerId){
    return queryFactory
        .select(Projections.constructor(WalkerTempUnAvailDate.class,
            walkerScheduleTemp.dateTime
            ))
        .from(user)
        .leftJoin(walkerScheduleTemp).on(walkerScheduleTemp.walkerId.eq(user.userId))
        .where(
            user.userId.eq(walkerId)
        ).fetch();
  }


  /**
   * 서비스 수행자의 시간당 비용 가져오기
   */
  @Override
  public List<WalkerTimePrice> walkerTimePrices(final Long walkerId){
    return queryFactory
        .select(Projections.constructor(WalkerTimePrice.class,
            walkerServicePrice.serviceFee,
                    walkerServicePrice.timeUnit
            ))
        .from(user)
        .leftJoin(walkerServicePrice).on(walkerServicePrice.walkerId.eq(user.userId))
        .where(
            user.userId.eq(walkerId)
        ).fetch();
  }

  /**
   * 해당 날짜에 예약된 날짜가 있는지 보내기
   */
  @Override
  public List<WalkerReserveInfo.Response> walkerReserveDate(final Long walkerId,final LocalDate checkReserveDate){
    return queryFactory
        .select(Projections.constructor(WalkerReserveInfo.Response.class,
            walkerReserveServiceInfo.serviceDateTime
        ))
        .from(walkerReserveServiceInfo)
        .rightJoin(walkerReserveServiceInfo.walker)
        .on(walkerReserveServiceInfo.walker.userId.eq(walkerId))
        .where(walkerReserveServiceInfo.status.eq(WALKER_ACCEPT)
                .and(walkerReserveServiceInfo.serviceDateTime.between(
                    checkReserveDate.atStartOfDay(), checkReserveDate.plusDays(1).atStartOfDay().minusSeconds(1)
                ))
        ).fetch();
  }

}
