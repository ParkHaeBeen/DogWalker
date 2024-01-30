package com.project.dogwalker.domain.user.walker;

import com.project.dogwalker.walkersearch.dto.response.WalkerPermUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.response.WalkerTempUnAvailDateResponse;
import com.project.dogwalker.walkersearch.dto.response.WalkerTimePriceResponse;
import java.time.LocalDate;
import java.util.List;

public interface WalkerInfoRepository {
  List <WalkerPermUnAvailDateResponse> walkerPermUnVailScheduleFindByWalkerId(Long walkerId);
  List<WalkerTempUnAvailDateResponse> walkerTempUnAvailFindByWalkerId(Long walkerId);
  List<WalkerReserveInfo.Response> walkerReserveDate( Long walkerId, LocalDate checkReserveDate);
  List<WalkerTimePriceResponse> walkerTimePrices(Long walkerId);
}
