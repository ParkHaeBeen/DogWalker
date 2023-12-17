package com.project.dogwalker.walkerservice.service;

import com.project.dogwalker.member.dto.MemberInfo;
import com.project.dogwalker.walkerservice.dto.RealTimeLocation;
import com.project.dogwalker.walkerservice.dto.ServiceCheckRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndRequest;
import com.project.dogwalker.walkerservice.dto.ServiceEndResponse;

public interface WalkerService {
  void checkService(MemberInfo memberInfo ,ServiceCheckRequest request);

  void startService(Long reserveId,int timeUnit);
  void saveRealTimeLocation(RealTimeLocation location);

  boolean isStartedService(Long reserveId);

  ServiceEndResponse saveServiceRoute(MemberInfo memberInfo, ServiceEndRequest request);

  void noticeCustomer(Long reserveId);
}
