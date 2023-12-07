package com.project.dogwalker.walkerList.service;

import com.project.dogwalker.domain.user.elastic.WalkerSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class WalkerListServiceImpl implements WalkerListService{

  private final WalkerSearchRepository walkerSearchRepository;


}
