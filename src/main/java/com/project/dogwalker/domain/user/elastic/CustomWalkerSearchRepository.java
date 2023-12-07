package com.project.dogwalker.domain.user.elastic;

import com.project.dogwalker.walkerList.dto.SearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomWalkerSearchRepository {
  Page <WalkerDocument> searchByName(final SearchCond searchCond,final Pageable pageable);
}
