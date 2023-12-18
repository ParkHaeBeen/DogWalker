package com.project.dogwalker.reserve.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.dto.ReserveStatusRequest;

public interface ReserveService {

  void isReserved(ReserveCheckRequest request);

  ReserveResponse reserveService(MemberInfo memberInfo, ReserveRequest request);

  ReserveCancel.Response reserveCancel(MemberInfo memberInfo , ReserveCancel.Request reserveId);

  void changeRequestServiceStatus(MemberInfo memberInfo , ReserveStatusRequest request);
}
