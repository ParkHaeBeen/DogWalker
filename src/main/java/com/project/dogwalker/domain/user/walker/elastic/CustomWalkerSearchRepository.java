package com.project.dogwalker.domain.user.walker.elastic;

import com.project.dogwalker.walkersearch.dto.request.WalkerInfoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomWalkerSearchRepository {
  Page <WalkerDocument> searchByName(final WalkerInfoRequest walkerInfoRequest , final
      Pageable pageable);
}
