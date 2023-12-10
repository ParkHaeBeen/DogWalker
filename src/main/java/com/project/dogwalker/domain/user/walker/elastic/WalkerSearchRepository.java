package com.project.dogwalker.domain.user.walker.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalkerSearchRepository extends ElasticsearchRepository<WalkerDocument, Long> , CustomWalkerSearchRepository {


}
