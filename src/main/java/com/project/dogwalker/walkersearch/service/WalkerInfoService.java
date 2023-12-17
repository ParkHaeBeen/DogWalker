package com.project.dogwalker.walkersearch.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfoSearchCond;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.WalkerUnAvailDetail;
import java.util.List;

public interface WalkerInfoService {

  List <WalkerInfo> getWalkerInfoList(MemberInfo memberInfo ,WalkerInfoSearchCond searchCond);

  WalkerUnAvailDetail getWalkerUnAvailService(Long walkerId);

  List<WalkerReserveInfo.Response> getReserveDate(WalkerReserveInfo.Request request);
}
