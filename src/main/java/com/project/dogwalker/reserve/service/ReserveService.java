package com.project.dogwalker.reserve.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;

public interface ReserveService {

  void isReserved(ReserveCheckRequest request);

  ReserveResponse reserveService(MemberInfo memberInfo, ReserveRequest request);
}
