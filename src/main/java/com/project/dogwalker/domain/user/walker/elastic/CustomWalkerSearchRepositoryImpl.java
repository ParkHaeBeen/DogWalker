package com.project.dogwalker.domain.user.walker.elastic;


import com.project.dogwalker.walkersearch.dto.request.WalkerInfoRequest;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;


@RequiredArgsConstructor
public class CustomWalkerSearchRepositoryImpl implements CustomWalkerSearchRepository{

  private final ElasticsearchOperations elasticsearchOperations;

  @Value("${spring.search.distance}")
  private Integer distanceMax;

  @Override
  public Page <WalkerDocument> searchByName(final WalkerInfoRequest walkerInfoRequest ,
      final Pageable pageable) {

    CriteriaQuery query=createCond(walkerInfoRequest ,pageable);
    SearchHits <WalkerDocument> searchHits = elasticsearchOperations.search(query , WalkerDocument.class);

    List <WalkerDocument> list = searchHits.stream().map(SearchHit::getContent)
        .collect(Collectors.toList());

    return new PageImpl <>(list, pageable, searchHits.getTotalHits());
  }


  private CriteriaQuery createCond(final WalkerInfoRequest walkerInfoRequest ,final Pageable pageable){
    Criteria nameCriteria = Criteria.where("walker_name").contains(walkerInfoRequest.getName())
        .and("location").within(new GeoPoint(walkerInfoRequest.getLat(), walkerInfoRequest.getLnt())
            ,distanceMax+"km");

    CriteriaQuery criteriaQuery = new CriteriaQuery(nameCriteria);
    criteriaQuery.setPageable(pageable);

    return criteriaQuery;
  }
}
