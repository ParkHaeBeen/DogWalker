package com.project.dogwalker.reserve.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.reserve.dto.ReserveCancel;
import com.project.dogwalker.reserve.dto.ReserveCheckRequest;
import com.project.dogwalker.reserve.dto.ReserveListResponse;
import com.project.dogwalker.reserve.dto.ReserveRequest;
import com.project.dogwalker.reserve.dto.ReserveResponse;
import com.project.dogwalker.reserve.dto.ReserveStatusRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReserveService {

  void isReserved(ReserveCheckRequest request);

  ReserveResponse reserveService(MemberInfo memberInfo, ReserveRequest request);

  ReserveCancel.Response reserveCancel(MemberInfo memberInfo , ReserveCancel.Request reserveId);

  void changeRequestServiceStatus(MemberInfo memberInfo , ReserveStatusRequest request);

  List <ReserveListResponse> getReserveList(MemberInfo memberInfo, Pageable pageable);
}
