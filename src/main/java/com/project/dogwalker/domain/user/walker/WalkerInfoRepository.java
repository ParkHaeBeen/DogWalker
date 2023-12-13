package com.project.dogwalker.domain.user.walker;

import com.project.dogwalker.walkerSearch.dto.WalkerPermUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerTempUnAvailDate;
import com.project.dogwalker.walkerSearch.dto.WalkerTimePrice;
import java.time.LocalDate;
import java.util.List;

public interface WalkerInfoRepository {
  List <WalkerPermUnAvailDate> walkerPermUnVailScheduleFindByWalkerId(Long walkerId);
  List<WalkerTempUnAvailDate> walkerTempUnAvailFindByWalkerId(Long walkerId);
  List<WalkerReserveInfo.Response> walkerReserveDate( Long walkerId, LocalDate checkReserveDate);


  List<WalkerTimePrice> walkerTimePrices(Long walkerId);
}
