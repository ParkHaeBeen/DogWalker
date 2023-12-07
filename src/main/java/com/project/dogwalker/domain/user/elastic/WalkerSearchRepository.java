package com.project.dogwalker.domain.user.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface WalkerSearchRepository extends ElasticsearchRepository<WalkerDocument, Long> , CustomWalkerSearchRepository {


}
