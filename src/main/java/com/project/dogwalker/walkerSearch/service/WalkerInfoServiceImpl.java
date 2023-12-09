package com.project.dogwalker.walkerSearch.service;

import com.project.dogwalker.domain.user.elastic.WalkerDocument;
import com.project.dogwalker.domain.user.elastic.WalkerSearchRepository;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoList;
import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalkerInfoServiceImpl implements WalkerInfoService {

  private final WalkerSearchRepository walkerSearchRepository;

  @Override
  @Transactional(readOnly = true)
  public List <WalkerInfoList> getWalkerInfoList(final WalkerInfoSearchCond searchCond) {
    final Page <WalkerDocument> walkerDocuments = walkerSearchRepository.searchByName(searchCond);

    return walkerDocuments.stream().map(WalkerInfoList::of)
        .collect(Collectors.toList());
  }
}
