package com.project.dogwalker.domain.user.elastic;

import com.project.dogwalker.walkerSearch.dto.WalkerInfoSearchCond;
import org.springframework.data.domain.Page;

public interface CustomWalkerSearchRepository {
  Page <WalkerDocument> searchByName(final WalkerInfoSearchCond walkerInfoSearchCond);
}
