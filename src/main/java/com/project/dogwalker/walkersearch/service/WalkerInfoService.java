package com.project.dogwalker.walkersearch.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkersearch.dto.WalkerInfoResponse;
import com.project.dogwalker.walkersearch.dto.WalkerInfoRequest;
import com.project.dogwalker.walkersearch.dto.WalkerReserveInfo;
import com.project.dogwalker.walkersearch.dto.WalkerUnAvailDetailResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface WalkerInfoService {

  List <WalkerInfoResponse> getWalkerInfoList(MemberInfo memberInfo , WalkerInfoRequest searchCond, Pageable pageable);

  WalkerUnAvailDetailResponse getWalkerUnAvailService(Long walkerId);

  List<WalkerReserveInfo.Response> getReserveDate(WalkerReserveInfo.Request request);
}
