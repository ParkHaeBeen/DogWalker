package com.project.dogwalker.walkerSearch.service;

import com.project.dogwalker.walkerSearch.dto.WalkerInfoList;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import java.util.List;

public interface WalkerInfoService {

  List <WalkerInfoList> getWalkerInfoList(WalkerInfoSearchCond searchCond);
}
