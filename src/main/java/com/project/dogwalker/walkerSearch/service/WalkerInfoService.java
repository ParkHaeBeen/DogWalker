package com.project.dogwalker.walkerSearch.service;

import com.project.dogwalker.walkerSearch.dto.WalkerInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkerSearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkerSearch.dto.WalkerUnAvailDetail;
import java.util.List;

public interface WalkerInfoService {

  List <WalkerInfo> getWalkerInfoList(WalkerInfoSearchCond searchCond);

  WalkerUnAvailDetail getWalkerUnAvailService(Long walkerId);

  List<WalkerReserveInfo.Response> getReserveDate(WalkerReserveInfo.Request request);
}
