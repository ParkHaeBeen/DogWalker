package com.project.dogwalker.domain.user.walker.elastic;

import com.project.dogwalker.walkersearch.dto.WalkerInfoSearchCond;
import org.springframework.data.domain.Page;

public interface CustomWalkerSearchRepository {
  Page <WalkerDocument> searchByName(final WalkerInfoSearchCond walkerInfoSearchCond);
}
