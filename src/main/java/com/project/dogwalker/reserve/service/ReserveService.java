package com.project.dogwalker.reserve.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.request.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.response.ReserveListResponse;
import com.project.dogwalker.reserve.dto.request.ReserveRequest;
import com.project.dogwalker.reserve.dto.response.ReserveResponse;
import com.project.dogwalker.reserve.dto.request.ReserveStatusRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReserveService {

  void isReserved(ReserveCheckRequest request);

  ReserveResponse reserveService(MemberInfo memberInfo, ReserveRequest request);

  ReserveCancel.Response reserveCancel(MemberInfo memberInfo , ReserveCancel.Request reserveId);

  void changeRequestServiceStatus(MemberInfo memberInfo , ReserveStatusRequest request);

  List <ReserveListResponse> getReserveList(MemberInfo memberInfo, Pageable pageable);

  ReserveResponse getReserveDetail(final MemberInfo memberInfo, final Long reserveId);
}
